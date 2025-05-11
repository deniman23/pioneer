package org.example.pioneer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pioneer.dto.dto.PhoneDto;
import org.example.pioneer.dto.request.PhoneRequest;
import org.example.pioneer.model.PhoneData;
import org.example.pioneer.service.PhoneDataService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/phones")
@RequiredArgsConstructor
@Validated
public class PhoneController {

    private final PhoneDataService phoneService;

    /**
     * GET /api/phones
     * Возвращает все телефоны текущего пользователя.
     */
    @GetMapping
    public List<PhoneDto> getAll(@AuthenticationPrincipal Long userId) {
        return phoneService.getAllForUser(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * POST /api/phones
     * Добавляет новый телефон текущему пользователю.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhoneDto add(@AuthenticationPrincipal Long userId,
                        @Valid @RequestBody PhoneRequest req) {
        PhoneData pd = phoneService.addPhone(userId, req.getPhone());
        return toDto(pd);
    }

    /**
     * PUT /api/phones/{phoneId}
     * Обновляет номер телефона текущего пользователя.
     */
    @PutMapping("/{phoneId}")
    public PhoneDto update(@AuthenticationPrincipal Long userId,
                           @PathVariable Long phoneId,
                           @Valid @RequestBody PhoneRequest req) {
        PhoneData pd = phoneService.updatePhone(userId, phoneId, req.getPhone());
        return toDto(pd);
    }

    /**
     * DELETE /api/phones/{phoneId}
     * Удаляет телефон текущего пользователя.
     */
    @DeleteMapping("/{phoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId,
                       @PathVariable Long phoneId) {
        phoneService.deletePhone(userId, phoneId);
    }

    private PhoneDto toDto(PhoneData pd) {
        return PhoneDto.builder()
                .id(pd.getId())
                .phone(pd.getPhone())
                .primaryFlag(pd.isPrimaryFlag())
                .build();
    }
}