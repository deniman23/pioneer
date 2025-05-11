package org.example.pioneer.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO для смены имени пользователя.
 */
@Data
public class NameUpdateRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
