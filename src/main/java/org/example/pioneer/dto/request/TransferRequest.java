package org.example.pioneer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull
    @Min(1)
    private Long toUserId;

    @NotNull
    @Min(value = 1, message = "Сумма должна быть >= 1")
    private BigDecimal amount;
}