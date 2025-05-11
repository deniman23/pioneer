package org.example.pioneer.repository;


import org.example.pioneer.model.PhoneData;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    @Cacheable(cacheNames = "phones", key = "#phone")
    Optional<PhoneData> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Cacheable(cacheNames = "phonesByUser", key = "#userId")
    List<PhoneData> findAllByUserId(Long userId);

    @Override
    @CacheEvict(cacheNames = {"phones", "phonesByUser"}, allEntries = true)
    <S extends PhoneData> S save(S entity);

    @Override
    @CacheEvict(cacheNames = {"phones", "phonesByUser"}, allEntries = true)
    void deleteById(Long id);
}
