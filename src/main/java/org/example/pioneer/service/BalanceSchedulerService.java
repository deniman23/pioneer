package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.model.Account;
import org.example.pioneer.repository.AccountRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceSchedulerService {

    private final AccountRepository accountRepo;
    private static final BigDecimal INTEREST_RATE = new BigDecimal("1.10");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");
    private static final int SCALE = 2;

    /**
     * Каждые 30 секунд начисляем 10%, но не более 207% от initialBalance.
     */
    @Scheduled(fixedRate = 30_000)
    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public void accrueInterest() {
        List<Account> accounts = accountRepo.findAll();
        for (Account acct : accounts) {
            BigDecimal current = acct.getBalance();
            BigDecimal initial = acct.getInitialBalance();

            // cap = initial * 2.07
            BigDecimal cap = initial.multiply(MAX_MULTIPLIER)
                    .setScale(SCALE, RoundingMode.HALF_EVEN);

            // newBalance = current * 1.10
            BigDecimal increased = current.multiply(INTEREST_RATE)
                    .setScale(SCALE, RoundingMode.HALF_EVEN);

            BigDecimal next = increased.min(cap);
            // если уже достигаем cap, дальше не растём
            if (next.compareTo(current) > 0) {
                acct.setBalance(next);
                accountRepo.save(acct);
            }
        }
    }
}