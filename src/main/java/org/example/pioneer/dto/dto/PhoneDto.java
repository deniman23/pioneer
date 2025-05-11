package org.example.pioneer.dto.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для вывода номера телефона пользователя.
 */
@Data
@Builder
public class PhoneDto {
    private Long id;
    private String phone;
    private boolean primaryFlag;
}