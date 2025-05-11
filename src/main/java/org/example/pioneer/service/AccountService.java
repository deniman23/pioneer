package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.Account;
import org.example.pioneer.repository.AccountRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepo;

    /**
     * Получить Account по userId.
     * @throws ResourceNotFoundException, если аккаунт не найден.
     */
    @Cacheable(value = "accounts", key = "#userId")
    public Account getByUserId(Long userId) {
        return accountRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account не найден для userId=" + userId)
                );
    }
}