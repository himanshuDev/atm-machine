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

import com.abcbank.constant.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {

    @NotBlank(message = "Username cannot be blank")
    private String userName;

    @Pattern(regexp = "^[0-9]{4}", message = "Pin can only be number and that to 4 digits only")
    @NotBlank(message = "PIN cannot be blank")
    private String pin;

    @Min(value = 1, message = "Minimum value for withdrawal is 1 " + StringConstants.CurrencyFormat)
    @NotNull(message = "Withdrawal amount cannot be empty")
    private Long withDrawlAmount;

    //If not provided its false
    private Boolean useOverDraft = false;

    @Override
    public String toString() {
        return "WithdrawalRequest{" +
                "userName='" + userName + '\'' +
                ", pin= ***** "+
                ", withDrawlAmount=" + withDrawlAmount +
                ", useOverDraft=" + useOverDraft +
                '}';
    }
}
