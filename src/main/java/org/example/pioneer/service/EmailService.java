package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.exception.EmailAlreadyExistsException;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.EmailData;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.EmailDataRepository;
import org.example.pioneer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailDataRepository emailRepo;
    private final UserRepository userRepo;

    /**
     * Получить все EmailData пользователя.
     * @throws ResourceNotFoundException, если пользователь не найден
     */
    @Transactional(readOnly = true)
    public List<EmailData> getAllForUser(Long userId) {
        ensureUserExists(userId);
        return emailRepo.findAllByUserId(userId);
    }

    /**
     * Добавить новый e-mail пользователю.
     * @throws EmailAlreadyExistsException, если e-mail уже занят
     * @throws ResourceNotFoundException, если пользователь не найден
     */
    @Transactional
    public EmailData addEmail(Long userId, String email) {
        ensureUserExists(userId);

        if (emailRepo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User не найден, id=" + userId));

        EmailData ed = EmailData.builder()
                .email(email)
                .primaryFlag(false)
                .user(user)
                .build();

        return emailRepo.save(ed);
    }

    /**
     * Изменить существующий e-mail (по его id).
     * @throws ResourceNotFoundException, если emailId не найден
     * @throws IllegalArgumentException, если e-mail принадлежит не этому пользователю
     * @throws EmailAlreadyExistsException, если новый e-mail уже занят
     */
    @Transactional
    public EmailData updateEmail(Long userId, Long emailId, String newEmail) {
        EmailData ed = emailRepo.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("EmailData не найден, id=" + emailId));

        if (!ed.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя менять чужой email");
        }
        if (emailRepo.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException(newEmail);
        }

        ed.setEmail(newEmail);
        return emailRepo.save(ed);
    }

    /**
     * Удалить e-mail по id.
     * Проверяет, чтобы после удаления у пользователя остался хотя бы один e-mail.
     * @throws ResourceNotFoundException, если emailId не найден
     * @throws IllegalArgumentException, если e-mail принадлежит не этому пользователю
     * @throws IllegalStateException, если это последний e-mail пользователя
     */
    @Transactional
    public void deleteEmail(Long userId, Long emailId) {
        EmailData ed = emailRepo.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("EmailData не найден, id=" + emailId));

        if (!ed.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя удалять чужой email");
        }

        long count = emailRepo.findAllByUserId(userId).size();
        if (count <= 1) {
            throw new IllegalStateException("У пользователя должен оставаться хотя бы один e-mail");
        }

        emailRepo.deleteById(emailId);
    }

    private void ensureUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User не найден, id=" + userId);
        }
    }
}