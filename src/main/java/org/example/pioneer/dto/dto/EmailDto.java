package org.example.pioneer.dto.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для вывода e-mail пользователя.
 */
@Data
@Builder
public class EmailDto {
    private Long id;
    private String email;
    private boolean primaryFlag;
}
