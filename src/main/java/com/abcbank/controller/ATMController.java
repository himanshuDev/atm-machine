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

package com.abcbank.controller;

import com.abcbank.aspects.CollectLog;
import com.abcbank.data.dto.BalanceEnquiry;
import com.abcbank.data.dto.BalanceEnquiryResponse;
import com.abcbank.data.dto.WithdrawalRequest;
import com.abcbank.data.dto.WithdrawalResponse;
import com.abcbank.data.entity.BankAccount;
import com.abcbank.data.entity.DenominationDetail;
import com.abcbank.service.bussiness.atm.ATMService;
import com.abcbank.service.bussiness.bankacc.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest Controller class. Exposes all the public APIs to the outside world.
 *
 * @author himanshuupadhyay
 */
@RestController
@RequestMapping("/atm/api")
public class ATMController {

    @Autowired
    private ATMService atmService;

    @Autowired
    private BankAccountService bankAccountService;

    /**
     * This controller method will fetch the denomination details from ATM. <br>
     * This is more of a audit feature. No authentication is added here.
     *
     * @return {@link com.abcbank.data.entity.DenominationDetail} A list of Denomination Details.
     */
    @CollectLog
    @Operation(summary = "Get all the Denomination details present in ATM Machine")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Will respond with denomination details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DenominationDetail.class))})})
    @GetMapping("atm-inventory")
    public List<DenominationDetail> getATMInventory() {
        return this.atmService.getATMInventory();
    }

    /**
     * This controller method will fetch the details of bank accounts. <br>
     * This is more of a audit feature. No authentication is added here.
     *
     * @return {@link com.abcbank.data.entity.BankAccount} A list of Denomination Details.
     */
    @CollectLog
    @Operation(summary = "Get all the accounts and their details form the bank")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Will respond with account details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class))})})
    @GetMapping("account-inventory")
    public List<BankAccount> getAccountInventory() {
        return this.bankAccountService.getAccountsInventory();
    }

    /**
     * <p>This controller method will authenticate the pin for user, On successful validation,
     * The requested users balance amount will be returned.</p>
     *
     * @param balanceEnquiry {@link com.abcbank.data.dto.BalanceEnquiry} A custom balance enquiry object.
     * @return {@link com.abcbank.data.dto.BalanceEnquiryResponse}. Bank account details for user.
     */
    @CollectLog
    @Operation(summary = "Get all the Balance for particular user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Will respond with account details for the particular user", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BalanceEnquiryResponse.class))})})
    @PostMapping("balance")
    public BalanceEnquiryResponse getBalanceForUser(@Valid @RequestBody BalanceEnquiry balanceEnquiry) {
        return this.bankAccountService.getBalanceForBalanceEnquiry(balanceEnquiry);
    }

    /**
     * <p>This controller method will authenticate, validates and performs the withdrawal.
     * Upon authentication[username, pin validation], validation[balance validation, atm amount limit],
     * the amount is withdrawn and a withdrawl response is sent to user.</p>
     *
     * @param withdrawalRequest {@link com.abcbank.data.dto.BalanceEnquiryResponse}
     * @return
     */
    @CollectLog
    @Operation(summary = "Withdraws the amount as requested," + " if there is sufficient money in account and atm has that much account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Will respond with account details", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class))})})
    @PostMapping("withdraw")
    public WithdrawalResponse withDraw(@Valid @RequestBody WithdrawalRequest withdrawalRequest) {
        return this.bankAccountService.withDraw(withdrawalRequest);
    }
}
