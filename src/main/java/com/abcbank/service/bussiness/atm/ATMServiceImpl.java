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

import com.abcbank.aspects.CollectLog;
import com.abcbank.constant.StringConstants;
import com.abcbank.data.dto.ATMInventory;
import com.abcbank.data.dto.CurrencyDispense;
import com.abcbank.data.dto.DispenseDetails;
import com.abcbank.data.entity.DenominationDetail;
import com.abcbank.data.service.ATMDataAccessService;
import com.abcbank.enums.ATMInventoryStatus;
import com.abcbank.exception.custom_exceptions.CurrencyDenominationCountNotAvailableException;
import com.abcbank.exception.custom_exceptions.CurrencyDenominationNotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ATMServiceImpl implements ATMService {

    @Autowired
    private ATMDataAccessService atmDataAccessService;

    @Override
    public List<DenominationDetail> getATMInventory() {
        return this.atmDataAccessService.getATMInventory();
    }

    /**
     * {@inheritDoc}
     *
     * @param amount Long the amount to be dispensed from the ATM.
     * @return
     */
    @Override
    @CollectLog
    public DispenseDetails canDispenseFromATM(Long amount) {
        if (Objects.isNull(amount) || amount <= 0) {
            return new DispenseDetails(null, StringConstants.AskToProvidePositiveAmount);
        }
        List<CurrencyDispense> currencyDispenses = new ArrayList<>();
        List<DenominationDetail> atmInventory = this.atmDataAccessService.getATMInventory();

        Long totalAmountInATM = atmInventory.stream().collect(Collectors.summingLong(dd -> dd.getCurrencyCount() * dd.getCurrencyCount()));
        if (totalAmountInATM < amount) {
            return new DispenseDetails(null, StringConstants.InsufficientAmountInATMMessage);
        }

        //Iterate over all currency and try to find the count for each denomination available in ATM.
        for (int inventoryIndex = 0; inventoryIndex < atmInventory.size() && amount != 0; inventoryIndex++) {
            DenominationDetail denominationDetail = atmInventory.get(inventoryIndex);
            Long neededCountForCurrentCurrency = amount / denominationDetail.getCurrency();
            neededCountForCurrentCurrency = neededCountForCurrentCurrency > denominationDetail.getCurrencyCount() ? denominationDetail.getCurrencyCount() : neededCountForCurrentCurrency;
            if (neededCountForCurrentCurrency > 0) {
                currencyDispenses.add(new CurrencyDispense(denominationDetail.getCurrency(), neededCountForCurrentCurrency.intValue(), StringConstants.CurrencyFormat));
                amount = amount - (denominationDetail.getCurrency() * neededCountForCurrentCurrency);
            }
        }
        if (amount != 0) {
            return new DispenseDetails(null, StringConstants.ImproperAmountToWithDraw);
        }
        return new DispenseDetails(currencyDispenses, StringConstants.CollectMoneyFromATMMessage);
    }

    /**
     * {@inheritDoc}
     *
     * @param dispenses the list of {@link CurrencyDispense} to be disposed.
     * @return
     */
    @Override
    @CollectLog
    public ATMInventory dispenseFormATM(List<CurrencyDispense> dispenses) {
        List<DenominationDetail> atmInventory = this.atmDataAccessService.getATMInventory();
        ATMInventory inventoryAfterDispense = this.removeDispensesFromInventory(atmInventory, dispenses);
        if (inventoryAfterDispense.getInventoryStatus().isHealthyWithdrawal()) {
            this.atmDataAccessService.saveAllCurrencyDetails(inventoryAfterDispense.getDenominationDetailList());
        }
        return inventoryAfterDispense;
    }

    /**
     * <p>This method removes the currency dispenses from the ATM inventory.</p>
     * This method also handles dispense failures by throwing execption such as:
     * <ul>
     *     <li>If requested currency denomination is not present in ATM.</li>
     *     <li>If requested currency count is not present in ATM.</li>
     * </ul>
     *
     * @param denominationDetails {@link DenominationDetail} a list of denominations from which dispense to be made.
     * @param dispenses           {@link CurrencyDispense} the details of dispense.
     * @return {@link ATMInventory} the atm inventory with the updated details.
     */
    private ATMInventory removeDispensesFromInventory(List<DenominationDetail> denominationDetails, List<CurrencyDispense> dispenses) {
        ATMInventory updatedInventory = null;
        try {
            Map<Integer, DenominationDetail> currencyLookup = denominationDetails.stream().collect(Collectors.toMap(DenominationDetail::getCurrency, atm -> atm));
            List<DenominationDetail> remainingDenominationDetails = dispenses.stream().map(dispense -> {
                DenominationDetail currencyDetail = currencyLookup.get(dispense.getCurrencyValue());

                // If requested currency is not found
                if (Objects.isNull(currencyDetail)) {
                    throw new CurrencyDenominationNotAvailableException("Denomination of value : " + dispense.getCurrencyValue() + " is not available in ATM");
                }
                // If requested currency count is not found
                if (currencyDetail.getCurrencyCount() < dispense.getCount()) {
                    throw new CurrencyDenominationCountNotAvailableException("Count for Denomination : " + dispense.getCurrencyValue() + " is not available in ATM : expected " + dispense.getCount() + ", Available : " + currencyDetail.getCurrencyCount());
                }

                currencyDetail.setCurrencyCount(currencyDetail.getCurrencyCount() - dispense.getCount());
                return currencyDetail;
            }).collect(Collectors.toList());

            updatedInventory = ATMInventory.of(remainingDenominationDetails, ATMInventoryStatus.DISPENSE_PERMITTED);
        } catch (CurrencyDenominationNotAvailableException exp) {
            updatedInventory = ATMInventory.of(exp.getMessage(), ATMInventoryStatus.DENOMINATION_NOT_AVAILABLE, denominationDetails);
        } catch (CurrencyDenominationCountNotAvailableException exp) {
            updatedInventory = ATMInventory.of(exp.getMessage(), ATMInventoryStatus.DENOMINATION_COUNT_NOT_AVAILABLE, denominationDetails);
        }

        return updatedInventory;
    }

}
