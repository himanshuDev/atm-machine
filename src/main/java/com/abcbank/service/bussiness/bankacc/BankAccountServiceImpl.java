/*
 * *
 *  * The MIT License (MIT)
 *  * <p>
 *  * Copyright (c) 2022
 *  * <p>
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  * <p>
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  * <p>
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.abcbank.service.bussiness.bankacc;

import com.abcbank.aspects.CollectLog;
import com.abcbank.constant.StringConstants;
import com.abcbank.data.dto.*;
import com.abcbank.data.entity.BankAccount;
import com.abcbank.data.service.BankAccountDataAccessService;
import com.abcbank.exception.custom_exceptions.AccountNotFoundException;
import com.abcbank.service.bussiness.atm.ATMService;
import com.abcbank.service.security.AuthenticationService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * This class provides the implementation for the BankAccountService.
 * All methods(except audit ones) are secured with the pin authentication
 *
 * @author himanshuupadhyay
 * </p>
 */
@Service
@Data
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BankAccountDataAccessService bankAccountDataAccessService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ATMService atmService;

    @Override
    public List<BankAccount> getAccountsInventory() {
        return this.bankAccountDataAccessService.getAllAccounts();
    }

    /**
     * {@inheritDoc}
     *
     * @param balanceEnquiry {@link BalanceEnquiry} details for getting account information.
     * @return
     */
    @Override
    @CollectLog
    public BalanceEnquiryResponse getBalanceForBalanceEnquiry(BalanceEnquiry balanceEnquiry) {
        if (Objects.isNull(balanceEnquiry)) {
            return new BalanceEnquiryResponse(null, ' ', StringConstants.InvalidBalanceEnquiry);
        }
        //Fetch bank details from DB.
        BankAccount bankAccount = null;
        try {
            bankAccount = this.bankAccountDataAccessService.getAccountDetailsForUserName(balanceEnquiry.getUserName());
        } catch (AccountNotFoundException exp) {
            return new BalanceEnquiryResponse(null, ' ', StringConstants.BankAccountNotFound);
        }


        //Authenticate
        if (this.authenticationService.authenticateBankAccount(bankAccount, balanceEnquiry.getPin())) {
            //Respond Balance.
            return new BalanceEnquiryResponse(bankAccount.getOpening_balance(), '£', "");
        } else {
            return new BalanceEnquiryResponse(null, ' ', StringConstants.InvalidPin);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @param withdrawalRequest
     * @return
     */
    @Transactional
    @Override
    @CollectLog
    public synchronized WithdrawalResponse withDraw(WithdrawalRequest withdrawalRequest) {
        WithdrawalResponse withdrawalResponse = new WithdrawalResponse();
        BankAccount bankAccount = this.bankAccountDataAccessService.getAccountDetailsForUserName(withdrawalRequest.getUserName());

        //[Contact 1]: Authenticate
        if (!this.authenticationService.authenticateBankAccount(bankAccount, withdrawalRequest.getPin())) {
            withdrawalResponse.prepareInvalidPinResponse(withdrawalRequest, StringConstants.InvalidPin);
            return withdrawalResponse;
        }

        //Check if withdrawal can be made from bank account.
        if (this.canWithDrawFromAccount(bankAccount, withdrawalRequest.getWithDrawlAmount(), withdrawalRequest.getUseOverDraft())) {

            //Get the DispenseDetails from ATM.
            DispenseDetails dispenseDetails = this.atmService.canDispenseFromATM(withdrawalRequest.getWithDrawlAmount());

            //If ATM permits dispense
            if (dispenseDetails.isDispensePermittedFromATM()) {
                // [Contract 2]: Withdraw
                bankAccount = this.performWithdrawalTransaction(bankAccount, withdrawalRequest, dispenseDetails.getCurrencyDispenseList());
                withdrawalResponse.prepareSuccessFullWithdrawResponse(bankAccount.getOpening_balance(), bankAccount.getOverdraft(), dispenseDetails.getCurrencyDispenseList(), withdrawalRequest, StringConstants.CollectMoneyFromATMMessage);
            } else {
                //Contract 3]: Dispense not permitted from ATM.
                withdrawalResponse.prepareInSufficientMoneyInATMResponse(bankAccount.getOpening_balance(), bankAccount.getOverdraft(), withdrawalRequest, dispenseDetails.getMessage());
            }
        } else {
            if (withdrawalRequest.getWithDrawlAmount() <= 0) {
                // [Contract 5] : Request for invalid amount withdrawal i.e. -ve or 0
                withdrawalResponse.prepareInvalidDispenseResponse(bankAccount.getOpening_balance(), bankAccount.getOverdraft(), withdrawalRequest, StringConstants.AskToProvidePositiveAmount);
            } else {

                // [Contract 4]: Low Balance in Account
                withdrawalResponse.prepareLowBalanceResponse(bankAccount.getOpening_balance(), bankAccount.getOverdraft(), withdrawalRequest, StringConstants.InsufficientAmountInAccountMessage);
            }
        }
        return withdrawalResponse;
    }

    /**
     * This method performs the withdrawal by
     * <ul>
     *  <li>Withdrawing the amount from ATM</li>
     *  <li>Dispense the withdrawal amount from the ATM</li>
     * </ul>
     *
     * @param bankAccount       {@link BankAccount} from which the withdrawal to be made.
     * @param withdrawalRequest {@link WithdrawalRequest} containing the details of amount to be disposed.
     * @param currencyDispenses A list of {@link CurrencyDispense} having details of denominations to be disposed.
     * @return {@link BankAccount} with the updated details of withdrawals.
     */
    @CollectLog
    private synchronized BankAccount performWithdrawalTransaction(BankAccount bankAccount, WithdrawalRequest withdrawalRequest, List<CurrencyDispense> currencyDispenses) {
        this.bankAccountDataAccessService.withDraw(bankAccount, withdrawalRequest.getWithDrawlAmount(), withdrawalRequest.getUseOverDraft());
        this.atmService.dispenseFormATM(currencyDispenses);
        return this.bankAccountDataAccessService.getAccountDetailsForUserName(withdrawalRequest.getUserName());
    }

    /**
     * <p>This method returns the total money present in the account.
     * <br>It honors the overdraft flag.</p>
     *
     * @param bankAccount  {@link BankAccount} from which the total amount needs to be fetched.
     * @param useOverDraft Boolean Determines if overdraft needs to considered while caclculting the amount or not.
     * @return total amount present in the account.
     */
    @CollectLog
    private Long getTotalAmountFromAccount(BankAccount bankAccount, Boolean useOverDraft) {
        Long totalMoneyInAcc = bankAccount.getOpening_balance();
        if (useOverDraft) {
            totalMoneyInAcc += bankAccount.getOverdraft();
        }
        return totalMoneyInAcc;
    }

    /**
     * <p>Determines if an amount can be withdrawn from the Bank account</p>
     *
     * @param bankAccount         {@link BankAccount} from which the amount to be withdrawn.
     * @param amountToBeWithdrawn Long actual amount to be withdrawn.
     * @param useOverDraft        Boolean if to use the overdraft feature or not.
     * @return Boolean true if the amount can be withdrawn, false otherwise.
     */
    @CollectLog
    private Boolean canWithDrawFromAccount(BankAccount bankAccount, Long amountToBeWithdrawn, Boolean useOverDraft) {
        if (Objects.nonNull(amountToBeWithdrawn) && amountToBeWithdrawn > 0) {
            Long totalBalanceInAccount = this.getTotalAmountFromAccount(bankAccount, useOverDraft);
            return totalBalanceInAccount >= amountToBeWithdrawn;
        }
        return false;
    }

}