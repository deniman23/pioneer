package org.example.pioneer.dto.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO для вывода информации о пользователе.
 */
@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
}