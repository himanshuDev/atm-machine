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

package com.abcbank.data.service;

import com.abcbank.data.entity.BankAccount;
import com.abcbank.data.repository.BankAccountRepository;
import com.abcbank.exception.custom_exceptions.AccountNotFoundException;
import com.abcbank.exception.custom_exceptions.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountDataAccessService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    public List<BankAccount> getAllAccounts() {
        List<BankAccount> bankAccounts = new ArrayList<>();
        this.bankAccountRepository.findAll().forEach(ba -> {
            ba.setAtm_pin(null);
            bankAccounts.add(ba);
        });
        return bankAccounts;
    }

    /**
     * @param userName
     * @return BankAccount if account exists, else returns null
     */
    public BankAccount getAccountDetailsForUserName(String userName) {
        Optional<BankAccount> possibleBankAccount = this.bankAccountRepository.findByUserName(userName);
        if (possibleBankAccount.isEmpty()) {
            throw new AccountNotFoundException("Account not exists for username " + userName);
        }
        return possibleBankAccount.get();
    }

    public synchronized void withDraw(BankAccount bankAccount, Long amount, Boolean useOverDraft) {
        if (amount <= bankAccount.getOpening_balance()) {
            bankAccount.setOpening_balance(bankAccount.getOpening_balance() - amount);
        } else {
            Long totalAmountIncludingOverdraftInAccount = bankAccount.getOpening_balance() + (useOverDraft ? bankAccount.getOverdraft() : 0);
            if (totalAmountIncludingOverdraftInAccount < amount) {
                throw new InsufficientFundsException("insufficient funds");
            } else {
                Long pendingOverDraft = totalAmountIncludingOverdraftInAccount - amount;
                bankAccount.setOverdraft(pendingOverDraft);
                bankAccount.setOpening_balance(0L);
            }
        }
        this.bankAccountRepository.save(bankAccount);
    }

}
