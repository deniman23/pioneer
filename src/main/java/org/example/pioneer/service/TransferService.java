package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.exception.InsufficientFundsException;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.Account;
import org.example.pioneer.repository.AccountRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountService accountService;
    private final AccountRepository accountRepo;

    /**
     * Перевод денег от одного пользователя к другому.
     * @param fromUserId  USER_ID отправителя (берётся из токена)
     * @param toUserId    USER_ID получателя
     * @param amount      сумма перевода, > 0
     * @throws IllegalArgumentException      если amount ≤ 0
     * @throws InsufficientFundsException    если на счёте отправителя недостаточно средств
     * @throws ResourceNotFoundException     если какой-то аккаунт не найден
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts", key = "#fromUserId"),
            @CacheEvict(value = "accounts", key = "#toUserId")
    })
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть > 0");
        }

        Account from = accountService.getByUserId(fromUserId);
        Account to   = accountService.getByUserId(toUserId);

        BigDecimal newFromBalance = from.getBalance().subtract(amount);
        if (newFromBalance.signum() < 0) {
            throw new InsufficientFundsException(fromUserId,
                    "недостаточно средств для перевода " + amount);
        }

        from.setBalance(newFromBalance);
        to.setBalance(to.getBalance().add(amount));

        // Сохраняем оба аккаунта; optimistic lock по @Version
        accountRepo.save(from);
        accountRepo.save(to);
    }
}