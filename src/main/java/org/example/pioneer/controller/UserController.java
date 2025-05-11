package org.example.pioneer.controller;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.dto.dto.UserDto;
import org.example.pioneer.dto.request.DateOfBirthUpdateRequest;
import org.example.pioneer.dto.request.NameUpdateRequest;
import org.example.pioneer.dto.request.PasswordUpdateRequest;
import org.example.pioneer.model.User;
import org.example.pioneer.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/{id}
     * Любой может получить любого пользователя по ID.
     */
    @GetMapping("/{id}")
    public UserDto getById(
            @PathVariable @Min(1) Long id
    ) {
        User u = userService.getById(id);
        return toDto(u);
    }

    /**
     * GET /api/users
     * Поиск пользователей с фильтрами и пагинацией.
     */
    @GetMapping
    public Page<UserDto> search(
            @RequestParam Optional<
                    LocalDate> dateOfBirthAfter,
            @RequestParam Optional<String> nameStartsWith,
            @RequestParam Optional<String> emailEquals,
            @RequestParam Optional<String> phoneEquals,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return userService.search(dateOfBirthAfter,
                        nameStartsWith,
                        emailEquals,
                        phoneEquals,
                        page, size)
                .map(this::toDto);
    }

    /**
     * PUT /api/users/me/name
     * Сменить имя текущего пользователя.
     */
    @PutMapping("/me/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateName(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody NameUpdateRequest req
    ) {
        userService.updateName(userId, req.getName());
    }

    /**
     * PUT /api/users/me/password
     * Сменить пароль текущего пользователя.
     * Принимаем уже захешированный пароль.
     */
    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordUpdateRequest req
    ) {
        userService.updatePassword(userId, req.getPassword());
    }

    /**
     * PUT /api/users/me/date-of-birth
     * Сменить дату рождения текущего пользователя.
     */
    @PutMapping("/me/date-of-birth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDateOfBirth(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DateOfBirthUpdateRequest req
    ) {
        userService.updateDateOfBirth(userId, req.getDateOfBirth());
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .dateOfBirth(u.getDateOfBirth())
                .build();
    }
}
