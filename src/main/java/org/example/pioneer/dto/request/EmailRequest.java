package org.example.pioneer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания/обновления e-mail.
 */
@Data
public class EmailRequest {

    @NotBlank
    @Email(message = "Неверный формат e-mail")
    private String email;
}
