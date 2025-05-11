package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.UserRepository;
import org.example.pioneer.repository.spec.UserSpecifications;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepo;

    /**
     * Получение пользователя по ID (для любого, 404 если не найден).
     */
    @Cacheable(value = "users", key = "#id")
    public User getById(@Min(1) Long id) {
        return userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User не найден, id=" + id)
                );
    }

    /**
     * Поиск пользователей с опциональной фильтрацией и пагинацией.
     * Фильтры:
     *  - dateOfBirthAfter: date_of_birth &gt; переданной даты
     *  - nameStartsWith: name LIKE '{prefix}%'
     *  - emailEquals: точное совпадение e-mail (JOIN на email_data)
     *  - phoneEquals: точное совпадение телефона (JOIN на phone_data)
     */
    public Page<User> search(Optional<LocalDate> dateOfBirthAfter,
                             Optional<String> nameStartsWith,
                             Optional<String> emailEquals,
                             Optional<String> phoneEquals,
                             @Min(0) int page,
                             @Min(1) int size) {

        Specification<User> spec = where(null);

        if (dateOfBirthAfter.isPresent()) {
            spec = spec.and(UserSpecifications.dateOfBirthAfter(dateOfBirthAfter.get()));
        }
        if (nameStartsWith.isPresent()) {
            spec = spec.and(UserSpecifications.nameStartsWith(nameStartsWith.get()));
        }
        if (emailEquals.isPresent()) {
            spec = spec.and(UserSpecifications.hasEmail(emailEquals.get()));
        }
        if (phoneEquals.isPresent()) {
            spec = spec.and(UserSpecifications.hasPhone(phoneEquals.get()));
        }

        Pageable pg = PageRequest.of(page, size, Sort.by("id").ascending());
        return userRepo.findAll(spec, pg);
    }

    /**
     * Сменить имя пользователя.
     * Может менять только сам пользователь (логика контроля в API/фильтрах безопасности).
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateName(@Min(1) Long userId,
                           @NotBlank String newName) {
        User u = getById(userId);
        u.setName(newName);
        userRepo.save(u);
    }

    /**
     * Сменить пароль. Здесь принимается уже захэши­рованный пароль.
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updatePassword(@Min(1) Long userId,
                               @NotBlank String newPasswordHash) {
        if (newPasswordHash.length() < 8) {
            throw new IllegalArgumentException("Пароль должен быть не менее 8 символов");
        }
        User u = getById(userId);
        u.setPasswordHash(newPasswordHash);
        userRepo.save(u);
    }

    /**
     * Сменить дату рождения.
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateDateOfBirth(@Min(1) Long userId,
                                  LocalDate newDateOfBirth) {
        if (newDateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
        User u = getById(userId);
        u.setDateOfBirth(newDateOfBirth);
        userRepo.save(u);
    }
}