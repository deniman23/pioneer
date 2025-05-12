package org.example.pioneer.service;

import org.example.pioneer.exception.InsufficientFundsException;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.Account;
import org.example.pioneer.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.function.LongFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тест без мокирования AccountService-класса.
 * Вместо него используем простой lambda-stub.
 */
class TransferServiceTest {

    private AccountRepository accountRepo;
    private TransferService transferService;

    // “Счётчики” для from/to
    private Account from;
    private Account to;

    @BeforeEach
    void setUp() {
        // чистый Mockito-мок для репозитория
        accountRepo = mock(AccountRepository.class);

        // тестовые аккаунты
        from = Account.builder()
                .id(1L)
                .balance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00"))
                .version(0L)
                .build();
        to = Account.builder()
                .id(2L)
                .balance(new BigDecimal("50.00"))
                .initialBalance(new BigDecimal("50.00"))
                .version(0L)
                .build();

        // Stub AccountService: возвращаем нужный объект по userId
        AccountService accountServiceStub = new AccountService(null) {
            @Override
            public Account getByUserId(Long userId) {
                if (userId.equals(1L)) return from;
                if (userId.equals(2L)) return to;
                throw new ResourceNotFoundException("no account for " + userId);
            }
        };

        transferService = new TransferService(accountServiceStub, accountRepo);
    }

    @Test
    void happyPath() {
        transferService.transfer(1L, 2L, new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), from.getBalance());
        assertEquals(new BigDecimal("80.00"), to.getBalance());

        // оба должны сохраниться
        verify(accountRepo).save(from);
        verify(accountRepo).save(to);
    }

    @Test
    void negativeOrZeroAmountThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("-5")));
    }

    @Test
    void insufficientFundsThrows() {
        assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("200.00")));
    }

    @Test
    void missingAccountThrows() {
        // передаём несуществующий userId
        assertThrows(ResourceNotFoundException.class,
                () -> transferService.transfer(99L, 2L, new BigDecimal("10.00")));
    }
}