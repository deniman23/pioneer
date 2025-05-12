package org.example.pioneer.repository;


import org.example.pioneer.model.Account;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Override
    @CacheEvict(cacheNames = "accounts", key = "#result.id")
    <S extends Account> S save(S entity);

    @Cacheable(cacheNames = "accounts", key = "#id")
    Optional<Account> findByUserId(Long userId);
}
