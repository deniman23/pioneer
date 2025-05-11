package org.example.pioneer.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


/**
 * DTO для создания/обновления номера телефона.
 */
@Data
public class PhoneRequest {

    @NotBlank(message = "Телефон не может быть пустым")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Телефон должен состоять из 10–15 цифр, опционально начинаясь с +"
    )
    private String phone;
}
