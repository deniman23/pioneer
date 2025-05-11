package org.example.pioneer.dto.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountDto {
    private Long userId;
    private BigDecimal balance;
    private BigDecimal initialBalance;
}