package org.example.pioneer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pioneer.dto.dto.AccountDto;
import org.example.pioneer.dto.request.TransferRequest;
import org.example.pioneer.model.Account;
import org.example.pioneer.service.AccountService;
import org.example.pioneer.service.TransferService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    /**
     * GET /api/account
     * Возвращает баланс и исходный депозит текущего пользователя.
     */
    @GetMapping
    public AccountDto getAccount(@AuthenticationPrincipal Long userId) {
        Account acct = accountService.getByUserId(userId);
        return AccountDto.builder()
                .userId(acct.getId())
                .balance(acct.getBalance())
                .initialBalance(acct.getInitialBalance())
                .build();
    }

    /**
     * POST /api/account/transfer
     * {
     *   "toUserId": 123,
     *   "amount": 10.50
     * }
     */
    @PostMapping("/transfer")
    public void transfer(@AuthenticationPrincipal Long fromUserId,
                         @Valid @RequestBody TransferRequest req) {
        transferService.transfer(fromUserId, req.getToUserId(), req.getAmount());
    }
}