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

package com.abcbank.data.dto;

import com.abcbank.enums.WithDrawStatus;
import com.abcbank.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponse {
    private String message;
    private WithDrawStatus withDrawlStatus;
    private Long remainingOpeningBalance;
    private Long remainingOverdraftAmount;

    private List<CurrencyDispense> currencyDispenseList;

    private WithdrawalRequest withdrawalRequest;

    public void prepareInvalidPinResponse(WithdrawalRequest withdrawalRequest,
                                          String... messages) {
        this.withDrawlStatus = WithDrawStatus.INVALID_PIN;
        this.message = StringUtils.combineMessage(messages);
        this.withdrawalRequest = withdrawalRequest;
    }

    public void prepareSuccessFullWithdrawResponse(
            Long remainingOpeningBalance,
            Long remainingOverdraftAmount,
            List<CurrencyDispense> currencyDispenseList,
            WithdrawalRequest withdrawalRequest,
            String... messages) {
        this.withDrawlStatus = WithDrawStatus.SUCCESS;
        this.currencyDispenseList = currencyDispenseList;
        this.remainingOpeningBalance = remainingOpeningBalance;
        this.remainingOverdraftAmount = remainingOverdraftAmount;
        this.message = StringUtils.combineMessage(messages);
        this.withdrawalRequest = withdrawalRequest;
        this.withdrawalRequest.setPin("*****");
    }

    public void prepareInvalidDispenseResponse(Long remainingOpeningBalance,
                                               Long remainingOverdraftAmount,
                                               WithdrawalRequest withdrawalRequest,
                                               String... messages) {
        this.withDrawlStatus = WithDrawStatus.INVALID_REQUEST_AMOUNT;
        this.remainingOpeningBalance = remainingOpeningBalance;
        this.remainingOverdraftAmount = remainingOverdraftAmount;
        this.withdrawalRequest = withdrawalRequest;
        this.message = StringUtils.combineMessage(messages);
        this.withdrawalRequest.setPin("*****");
    }

    public void prepareLowBalanceResponse(Long remainingOpeningBalance,
                                          Long remainingOverdraftAmount,
                                          WithdrawalRequest withdrawalRequest,
                                          String... messages) {
        this.withDrawlStatus = WithDrawStatus.LOW_BALANCE_IN_ACCOUNT;
        this.remainingOpeningBalance = remainingOpeningBalance;
        this.remainingOverdraftAmount = remainingOverdraftAmount;
        this.withdrawalRequest = withdrawalRequest;
        this.message = StringUtils.combineMessage(messages);
        this.withdrawalRequest.setPin("*****");
    }

    public void prepareInSufficientMoneyInATMResponse(Long remainingOpeningBalance,
                                                      Long remainingOverdraftAmount,
                                                      WithdrawalRequest withdrawalRequest,
                                                      String... messages) {
        this.withDrawlStatus = WithDrawStatus.LOW_BALANCE_IN_ATM;
        this.remainingOpeningBalance = remainingOpeningBalance;
        this.remainingOverdraftAmount = remainingOverdraftAmount;
        this.message = StringUtils.combineMessage(messages);
        this.withdrawalRequest = withdrawalRequest;
        this.withdrawalRequest.setPin("*****");
    }

}
