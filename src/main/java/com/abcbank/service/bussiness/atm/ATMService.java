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

package com.abcbank.service.bussiness.atm;

import com.abcbank.data.dto.ATMInventory;
import com.abcbank.data.dto.CurrencyDispense;
import com.abcbank.data.dto.DispenseDetails;
import com.abcbank.data.entity.DenominationDetail;

import java.util.List;

/**
 * <p>
 * This is the contract to implement the ATM service.
 * ATMService should allow the user to get the ATM inventory, Check if the requested amount can be
 * dispensed from the ATM and dispense the amount.
 * </p>
 */
public interface ATMService {

    /**
     * Fetch all the denomination details available in the ATM.
     *
     * @return {@link DenominationDetail} of all available denomination in the ATM.
     */
    List<DenominationDetail> getATMInventory();

    /**
     * <p>
     * Returns Dispense details for the requested amount from the ATM.
     * This method should handle all scenarios such as
     * <ul>
     *     <li>Insufficient in the ATM></li>
     *     <li>Invalid request to dispense i.e. null, negative</li>
     *     <li>Withdrawals which cannot be disposed, i.e requests such as 1234, even though<br>
     *     ATM has money but Denominations cannot be combined to get to the requested sum.
     *     </li>
     * </ul>
     * </p>
     *
     * @param amount Long the amount to be dispensed from the ATM.
     * @return {@linkplain DispenseDetails} for the amount to be dispense
     */
    DispenseDetails canDispenseFromATM(Long amount);

    /**
     * <p>Dispenses the provided dispenses form the ATM.</p>
     *
     * @param dispenses the list of {@link CurrencyDispense} to be disposed.
     * @return {@link ATMInventory} representing current state of ATM invntory.
     */
    ATMInventory dispenseFormATM(List<CurrencyDispense> dispenses);

}
