package org.example.pioneer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pioneer.dto.dto.EmailDto;
import org.example.pioneer.dto.request.EmailRequest;
import org.example.pioneer.model.EmailData;
import org.example.pioneer.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Validated
public class EmailController {

    private final EmailService emailService;

    /**
     * GET /api/emails
     * Получить все e-mail текущего пользователя.
     */
    @GetMapping
    public List<EmailDto> getAll(@AuthenticationPrincipal Long userId) {
        return emailService.getAllForUser(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * POST /api/emails
     * Добавить новый e-mail текущему пользователю.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmailDto add(@AuthenticationPrincipal Long userId,
                        @Valid @RequestBody EmailRequest req) {
        EmailData created = emailService.addEmail(userId, req.getEmail());
        return toDto(created);
    }

    /**
     * PUT /api/emails/{emailId}
     * Обновить существующий e-mail текущего пользователя.
     */
    @PutMapping("/{emailId}")
    public EmailDto update(@AuthenticationPrincipal Long userId,
                           @PathVariable Long emailId,
                           @Valid @RequestBody EmailRequest req) {
        EmailData updated = emailService.updateEmail(userId, emailId, req.getEmail());
        return toDto(updated);
    }

    /**
     * DELETE /api/emails/{emailId}
     * Удалить e-mail текущего пользователя.
     */
    @DeleteMapping("/{emailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId,
                       @PathVariable Long emailId) {
        emailService.deleteEmail(userId, emailId);
    }

    private EmailDto toDto(EmailData ed) {
        return EmailDto.builder()
                .id(ed.getId())
                .email(ed.getEmail())
                .primaryFlag(ed.isPrimaryFlag())
                .build();
    }
}