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

package com.abcbank.bussiness.bankaccount;

import com.abcbank.constant.StringConstants;
import com.abcbank.data.dto.*;
import com.abcbank.data.entity.BankAccount;
import com.abcbank.data.service.BankAccountDataAccessService;
import com.abcbank.enums.WithDrawStatus;
import com.abcbank.exception.custom_exceptions.AccountNotFoundException;
import com.abcbank.service.bussiness.atm.ATMService;
import com.abcbank.service.bussiness.bankacc.BankAccountServiceImpl;
import com.abcbank.service.security.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class BankAccountServiceTest {

    @Mock
    private BankAccountDataAccessService bankAccountDataAccessService;

    @Mock
    private ATMService atmService;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Autowired
    private AuthenticationService authenticationService;


    @BeforeEach
    public void setUp() {
        when(bankAccountDataAccessService.getAllAccounts()).then(new Answer<List<BankAccount>>() {
            @Override
            public List<BankAccount> answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<BankAccount> bankAccounts = new ArrayList<>();
                bankAccounts.add(new BankAccount(1L, "clint", "eastwood", "clint_west", "eastwood@wildwest.com", "123456789", "gd6/yf/26JU=", 800L, 200L));
                bankAccounts.add(new BankAccount(2L, "russell", "crowe", "russell_gladiator", "maximus@gladiator.com", "987654321", "7jh4Sd0w2ZY=", 1800L, 150L));
                return bankAccounts;
            }
        });

        when(bankAccountDataAccessService.getAccountDetailsForUserName("clint_west")).then(new Answer<BankAccount>() {
            @Override
            public BankAccount answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new BankAccount(1L, "clint", "eastwood", "clint_west", "eastwood@wildwest.com", "123456789", "gd6/yf/26JU=", 800L, 200L);
            }
        });

        when(bankAccountDataAccessService.getAccountDetailsForUserName("russell_gladiator")).then(new Answer<BankAccount>() {
            @Override
            public BankAccount answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new BankAccount(2L, "russell", "crowe", "russell_gladiator", "maximus@gladiator.com", "987654321", "7jh4Sd0w2ZY=", 1800L, 150L);
            }
        });

        when(bankAccountDataAccessService.getAccountDetailsForUserName("unknown")).then(new Answer<BankAccount>() {
            @Override
            public BankAccount answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw new AccountNotFoundException("Account not exists for username unknown");
            }
        });

        when(this.atmService.canDispenseFromATM(1800L)).then(new Answer<DispenseDetails>() {
            @Override
            public DispenseDetails answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new DispenseDetails(null, StringConstants.InsufficientAmountInATMMessage);
            }
        });

        when(this.atmService.canDispenseFromATM(1000L)).then(new Answer<DispenseDetails>() {
            @Override
            public DispenseDetails answer(InvocationOnMock invocationOnMock) throws Throwable {
                ArrayList<CurrencyDispense> cdl = new ArrayList<>();
                cdl.add(CurrencyDispense.of(50, 10));
                cdl.add(CurrencyDispense.of(20, 25));
                return new DispenseDetails(cdl, StringConstants.CollectMoneyFromATMMessage);
            }
        });

        when(this.atmService.canDispenseFromATM(800L)).then(new Answer<DispenseDetails>() {
            @Override
            public DispenseDetails answer(InvocationOnMock invocationOnMock) throws Throwable {
                ArrayList<CurrencyDispense> cdl = new ArrayList<>();
                cdl.add(CurrencyDispense.of(50, 10));
                cdl.add(CurrencyDispense.of(20, 15));
                return new DispenseDetails(cdl, StringConstants.CollectMoneyFromATMMessage);
            }
        });

        this.bankAccountService.setAuthenticationService(this.authenticationService);
    }

    @Test
    public void testMockData() {
        List<BankAccount> listOfAccounts = this.bankAccountService.getAccountsInventory();
        Assertions.assertEquals(2, listOfAccounts.size());
    }

    @Test
    public void testSuccessBalanceEnquiry() {
        BalanceEnquiry be = new BalanceEnquiry("1234", "clint_west");
        BalanceEnquiryResponse ber = this.bankAccountService.getBalanceForBalanceEnquiry(be);
        Assertions.assertEquals(800, ber.getBalance());
    }

    @Test
    public void testIncorrectPinBalanceEnquiry() {
        BalanceEnquiry be = new BalanceEnquiry("12343", "clint_west");
        BalanceEnquiryResponse ber = this.bankAccountService.getBalanceForBalanceEnquiry(be);
        Assertions.assertTrue(StringConstants.InvalidPin.equals(ber.getMessage()));
    }

    @Test
    public void testIncorrectUserBalanceEnquiry() {
        BalanceEnquiry be = new BalanceEnquiry("12343", "unknown");
        BalanceEnquiryResponse ber = this.bankAccountService.getBalanceForBalanceEnquiry(be);
        Assertions.assertTrue(StringConstants.BankAccountNotFound.equals(ber.getMessage()));
    }


    @Test
    public void testNullBalanceEnquiry() {
        BalanceEnquiryResponse ber = this.bankAccountService.getBalanceForBalanceEnquiry(null);
        Assertions.assertTrue(StringConstants.InvalidBalanceEnquiry.equals(ber.getMessage()));
    }

    @Test
    public void testWithDrawSuccess() {
        WithdrawalRequest wdr = new WithdrawalRequest("clint_west", "1234", 800L, false);
        WithdrawalResponse wdresp = this.bankAccountService.withDraw(wdr);
        Assertions.assertTrue(WithDrawStatus.SUCCESS.equals(wdresp.getWithDrawlStatus()));
    }

    @Test
    public void testWithDrawWithInvalidPin() {
        WithdrawalRequest wdr = new WithdrawalRequest("clint_west", "12343", 500L, false);
        WithdrawalResponse wdresp = this.bankAccountService.withDraw(wdr);
        Assertions.assertTrue(WithDrawStatus.INVALID_PIN.equals(wdresp.getWithDrawlStatus()));
    }

    @Test
    public void testWithDrawWithLowBalanceInAccount() {
        WithdrawalRequest wdr = new WithdrawalRequest("clint_west", "1234", 50000L, false);
        WithdrawalResponse wdresp = this.bankAccountService.withDraw(wdr);
        Assertions.assertTrue(WithDrawStatus.LOW_BALANCE_IN_ACCOUNT.equals(wdresp.getWithDrawlStatus()));
    }

    @Test
    public void testwithDrawWithOverDraft() {
        WithdrawalRequest wdr = new WithdrawalRequest("clint_west", "1234", 1000L, true);
        WithdrawalResponse wdresp = this.bankAccountService.withDraw(wdr);
        Assertions.assertTrue(WithDrawStatus.SUCCESS.equals(wdresp.getWithDrawlStatus()));
    }

    @Test
    public void testWithDrawWithLowBalanceInATM() {
        WithdrawalRequest wdr = new WithdrawalRequest("russell_gladiator", "4321", 1800L, true);
        WithdrawalResponse wdresp = this.bankAccountService.withDraw(wdr);
        Assertions.assertTrue(WithDrawStatus.LOW_BALANCE_IN_ATM.equals(wdresp.getWithDrawlStatus()));
    }

    @Test
    public void testWithDrawWithNegativeAmount() {
        WithdrawalRequest wdr = new WithdrawalRequest("russell_gladiator", "4321", -1800L, true);
        WithdrawalResponse wdresp = this.bankAccountService.withDraw(wdr);
        Assertions.assertTrue(WithDrawStatus.INVALID_REQUEST_AMOUNT.equals(wdresp.getWithDrawlStatus()));
    }

}
