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

package com.abcbank.bussiness.atm;

import com.abcbank.constant.StringConstants;
import com.abcbank.data.dto.ATMInventory;
import com.abcbank.data.dto.CurrencyDispense;
import com.abcbank.data.dto.DispenseDetails;
import com.abcbank.data.entity.DenominationDetail;
import com.abcbank.data.service.ATMDataAccessService;
import com.abcbank.enums.ATMInventoryStatus;
import com.abcbank.service.bussiness.atm.ATMServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class ATMServiceTest {

    @Mock
    private ATMDataAccessService atmDataAccessService;

    @InjectMocks
    private ATMServiceImpl atmService;


    @BeforeEach
    public void setUp() {
        when(atmDataAccessService.getATMInventory()).then(new Answer<List<DenominationDetail>>() {
            @Override
            public List<DenominationDetail> answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<DenominationDetail> atmDepositedMoney = new ArrayList<>();
                atmDepositedMoney.add(new DenominationDetail(1L, 50, 10, '£'));
                atmDepositedMoney.add(new DenominationDetail(2L, 20, 30, '£'));
                atmDepositedMoney.add(new DenominationDetail(3L, 10, 30, '£'));
                atmDepositedMoney.add(new DenominationDetail(4L, 5, 20, '£'));
                return atmDepositedMoney;
            }
        });
    }

    @Test
    public void testCanDispenseFromATMSuccessfully() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(500L);
        Assertions.assertEquals(1, dd.getCurrencyDispenseList().size());
        Assertions.assertEquals(500, dd.getTotalMoneyWithThisDispense());
        Assertions.assertTrue(StringConstants.CollectMoneyFromATMMessage.equals(dd.getMessage()));
    }

    @Test
    public void testCanDispenseFromATMInSufficientMoneyInATM() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(5000L);
        Assertions.assertEquals(null, dd.getCurrencyDispenseList());
        Assertions.assertTrue(StringConstants.InsufficientAmountInATMMessage.equals(dd.getMessage()));
    }

    @Test
    public void testCanDispenseFromATMWithMultipleDenominations() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(1300L);

        // Dispensed Amount should be available in 3 denominations.
        Assertions.assertEquals(3, dd.getCurrencyDispenseList().size());

        // 50 £ should be 10
        Assertions.assertEquals(50, dd.getCurrencyDispenseList().get(0).getCurrencyValue());
        Assertions.assertEquals(10, dd.getCurrencyDispenseList().get(0).getCount());

        // 20 £ should be 30
        Assertions.assertEquals(20, dd.getCurrencyDispenseList().get(1).getCurrencyValue());
        Assertions.assertEquals(30, dd.getCurrencyDispenseList().get(1).getCount());

        // 10 £ should be 20
        Assertions.assertEquals(10, dd.getCurrencyDispenseList().get(2).getCurrencyValue());
        Assertions.assertEquals(20, dd.getCurrencyDispenseList().get(2).getCount());

        Assertions.assertTrue(StringConstants.CollectMoneyFromATMMessage.equals(dd.getMessage()));
    }

    @Test
    public void testCanDispenseNegativeAmount() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(-1L);
        Assertions.assertEquals(null, dd.getCurrencyDispenseList());
        Assertions.assertTrue(StringConstants.AskToProvidePositiveAmount.equals(dd.getMessage()));
    }

    @Test
    public void testCanDispenseNullAmount() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(null);
        Assertions.assertEquals(null, dd.getCurrencyDispenseList());
        Assertions.assertTrue(StringConstants.AskToProvidePositiveAmount.equals(dd.getMessage()));
    }

    @Test
    public void testCanDispenseZeroAmount() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(0L);
        Assertions.assertEquals(null, dd.getCurrencyDispenseList());
        Assertions.assertTrue(StringConstants.AskToProvidePositiveAmount.equals(dd.getMessage()));
    }

    @Test
    public void testSuccessUpdateDispenseDetailsInATM() {
        List<CurrencyDispense> dispenses = new ArrayList<>();
        dispenses.add(CurrencyDispense.of(50, 5));

        ATMInventory updatedInventory = this.atmService.dispenseFormATM(dispenses);
        Assertions.assertTrue(updatedInventory.getInventoryStatus().isHealthyWithdrawal());
    }

    @Test
    public void testDenominationCountNotAvailableInATM() {
        List<CurrencyDispense> dispenses = new ArrayList<>();
        dispenses.add(CurrencyDispense.of(50, 500));

        ATMInventory updatedInventory = this.atmService.dispenseFormATM(dispenses);
        Assertions.assertTrue(ATMInventoryStatus.DENOMINATION_COUNT_NOT_AVAILABLE.equals(updatedInventory.getInventoryStatus()));
    }

    @Test
    public void testDenominationNotAvailableInATM() {
        List<CurrencyDispense> dispenses = new ArrayList<>();
        dispenses.add(CurrencyDispense.of(1000, 5));

        ATMInventory updatedInventory = this.atmService.dispenseFormATM(dispenses);
        Assertions.assertTrue(ATMInventoryStatus.DENOMINATION_NOT_AVAILABLE.equals(updatedInventory.getInventoryStatus()));
    }

    @Test
    public void testImproperAmountToWithdraw() {
        DispenseDetails dd = this.atmService.canDispenseFromATM(123L);
        Assertions.assertEquals(null, dd.getCurrencyDispenseList());
        Assertions.assertTrue(StringConstants.ImproperAmountToWithDraw.equals(dd.getMessage()));
    }

}
