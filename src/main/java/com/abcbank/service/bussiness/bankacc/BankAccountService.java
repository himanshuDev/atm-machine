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

import com.abcbank.data.dto.BalanceEnquiry;
import com.abcbank.data.dto.BalanceEnquiryResponse;
import com.abcbank.data.dto.WithdrawalRequest;
import com.abcbank.data.dto.WithdrawalResponse;
import com.abcbank.data.entity.BankAccount;

import java.util.List;

/**
 * <p>
 * This is the contract to implement the Bank Account Services.
 * Bank Account Service should allow the user to get the data from all accounts,<br
 * respond to the balance enquiry with balance enquiry response and allow the user <br>
 * to withdraw the money from user's account. <br>
 * All the method implementations should be guarded with the Authentication block. i.e. <br>
 * all methods should validate pin before performing any operation.{except audit methods.}
 * dispensed from the ATM and dispense the amount.
 * </p>
 */
public interface BankAccountService {

    /**
     * This method should return the entire state of bank inventory.
     * <br> This is more for the audit purpose.
     *
     * @return
     */
    List<BankAccount> getAccountsInventory();

    /**
     * <p>This method should return the details of the account as BalanceEnquiryResponse.</p>
     * <br>This method should check the ATM PIN from the balanceEnquiry request and authenticate against the user pin <br>
     * On successful authentication this method should provide the details of account <br>
     * else should respond with the BalanceEnquiryResponse as ATM pin not correct.
     *
     * @param balanceEnquiry {@link BalanceEnquiry} details for getting account information.
     * @return {@link BalanceEnquiryResponse} A balance enquiry response for the requested {@BalanceEnquiry}
     */
    BalanceEnquiryResponse getBalanceForBalanceEnquiry(BalanceEnquiry balanceEnquiry);

    /**
     * <p>This method should perform the withdraw from the user account
     * This method should check the ATM PIN from the withdrawal request and authenticate against the user pin <br>
     * This operation must be performed in the synchronized way.
     * If the withdrawal request is invalid then an appropriate response should be returned.
     * this method should be able to handle the scenarios like
     * <ul>
     *     <li>Do not process for Invalid pin</li>
     *     <li>Genuine Withdrawal</li>
     *     <li>Reject if amount is not available in ATM.</li>
     *     <li>Low balance in the account.</li>
     *     <li>Improper withdrawal request such as, -ive amount, 0 withdrawal etc.</li>
     * </ul>
     * </p>
     *
     * @param withdrawalRequest
     * @return
     */
    WithdrawalResponse withDraw(WithdrawalRequest withdrawalRequest);
}
