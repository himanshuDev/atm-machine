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

package com.abcbank.enums;

public enum WithDrawStatus {
    SUCCESS("withdrawn successfully", "Indicates if the amount is withdrawn from account successfully"),
    INVALID_PIN("invalid pin", "Indicates if the atm pin is incorrect."),
    LOW_BALANCE_IN_ACCOUNT("low_balance", "Indicates if the amount in account is insufficient than requested amount"),
    LOW_BALANCE_IN_ATM("low_balance_in_atm", "Indicates if the amount in atm is insufficient than requested amount"),

    INVALID_REQUEST_AMOUNT("invalid request amount", "Indicates if the amount requested is 0 or -ive");

    private final String name;
    private final String description;

    WithDrawStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "WithDrawlStatus{" +
                "name='" + name + '\'' +
                '}';
    }
}
