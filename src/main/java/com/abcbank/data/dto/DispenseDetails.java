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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispenseDetails {
    private List<CurrencyDispense> currencyDispenseList;
    private String message;

    public Boolean isDispensePermittedFromATM() {
        return Objects.nonNull(this.currencyDispenseList) && this.currencyDispenseList.size() > 0;
    }

    /**
     * @return total amount in dispense if dispense is valid else -1;
     */
    public Integer getTotalMoneyWithThisDispense() {
        if (this.isDispensePermittedFromATM()) {
            return this.currencyDispenseList.stream().collect(Collectors.summingInt(CurrencyDispense::getTotal));
        }
        return -1;
    }
}
