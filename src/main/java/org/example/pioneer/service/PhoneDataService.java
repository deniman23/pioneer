package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.exception.PhoneAlreadyExistsException;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.PhoneData;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.PhoneDataRepository;
import org.example.pioneer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhoneDataService {

    private final PhoneDataRepository phoneRepo;
    private final UserRepository userRepo;

    /**
     * Получить все телефоны пользователя.
     * @throws ResourceNotFoundException если пользователь не найден.
     */
    @Transactional(readOnly = true)
    public List<PhoneData> getAllForUser(Long userId) {
        ensureUserExists(userId);
        return phoneRepo.findAllByUserId(userId);
    }

    /**
     * Добавить телефон пользователю.
     * Проверяет, что телефон ещё не занят.
     */
    @Transactional
    public PhoneData addPhone(Long userId, String phone) {
        ensureUserExists(userId);

        if (phoneRepo.existsByPhone(phone)) {
            throw new PhoneAlreadyExistsException(phone);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User не найден, id=" + userId));

        PhoneData pd = PhoneData.builder()
                .phone(phone)
                .primaryFlag(false)
                .user(user)
                .build();

        return phoneRepo.save(pd);
    }

    /**
     * Обновить существующий телефон (по его id).
     * Проверяет, что новый номер не занят и что телефон принадлежит этому пользователю.
     */
    @Transactional
    public PhoneData updatePhone(Long userId, Long phoneId, String newPhone) {
        PhoneData pd = phoneRepo.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("PhoneData не найден, id=" + phoneId));

        if (!pd.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя менять чужой телефон");
        }
        if (phoneRepo.existsByPhone(newPhone)) {
            throw new PhoneAlreadyExistsException(newPhone);
        }

        pd.setPhone(newPhone);
        return phoneRepo.save(pd);
    }

    /**
     * Удалить телефон по id.
     * Проверяет, что телефон принадлежит пользователю и что после удаления останется хотя бы один.
     */
    @Transactional
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData pd = phoneRepo.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("PhoneData не найден, id=" + phoneId));

        if (!pd.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя удалять чужой телефон");
        }

        // Проверяем, что после удаления останется хотя бы 1 телефон
        long cnt = phoneRepo.findAllByUserId(userId).stream().count();
        if (cnt <= 1) {
            throw new IllegalStateException("У пользователя должен оставаться хотя бы один телефон");
        }

        phoneRepo.deleteById(phoneId);
    }

    private void ensureUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User не найден, id=" + userId);
        }
    }
}