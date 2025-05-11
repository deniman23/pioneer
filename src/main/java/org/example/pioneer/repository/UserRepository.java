package org.example.pioneer.repository;


import org.example.pioneer.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Override
    @Cacheable(cacheNames = "users", key = "#id")
    Optional<User> findById(Long id);

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    <S extends User> S save(S entity);

    @Override
    @CacheEvict(cacheNames = "users", key = "#id")
    void deleteById(Long id);

    // удобный метод по логину, пригодится при аутентификации
    @Cacheable(cacheNames = "users", key = "#login")
    Optional<User> findByLogin(String login);
}
