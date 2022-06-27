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

import com.abcbank.data.entity.DenominationDetail;
import com.abcbank.enums.ATMInventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ATMInventory {
    private ATMInventoryStatus inventoryStatus;
    private List<DenominationDetail> denominationDetailList;
    private String messsage;

    public static ATMInventory of(List<DenominationDetail> denominationDetailList, ATMInventoryStatus inventoryStatus) {
        ATMInventory atmInventory = new ATMInventory();
        atmInventory.setDenominationDetailList(denominationDetailList);
        atmInventory.setInventoryStatus(ATMInventoryStatus.DISPENSE_PERMITTED);
        return atmInventory;
    }

    public static ATMInventory of(String message, ATMInventoryStatus inventoryStatus, List<DenominationDetail> denominationDetailList) {
        ATMInventory atmInventory = new ATMInventory();
        atmInventory.setDenominationDetailList(denominationDetailList);
        atmInventory.setInventoryStatus(inventoryStatus);
        atmInventory.setMesssage(message);
        return atmInventory;
    }

}
