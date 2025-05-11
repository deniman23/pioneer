package org.example.pioneer.repository;


import org.example.pioneer.model.EmailData;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    @Cacheable(cacheNames = "emails", key = "#email")
    Optional<EmailData> findByEmail(String email);

    boolean existsByEmail(String email);

    @Cacheable(cacheNames = "emailsByUser", key = "#userId")
    List<EmailData> findAllByUserId(Long userId);

    @Override
    @CacheEvict(cacheNames = {"emails", "emailsByUser"}, allEntries = true)
    <S extends EmailData> S save(S entity);

    @Override
    @CacheEvict(cacheNames = {"emails", "emailsByUser"}, allEntries = true)
    void deleteById(Long id);
}
