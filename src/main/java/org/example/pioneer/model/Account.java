package org.example.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(
            name = "balance",
            nullable = false,
            precision = 19,
            scale = 4
    )
    @NotNull
    private BigDecimal balance;

    @Column(
            name = "initial_balance",
            nullable = false,
            precision = 19,
            scale = 4
    )
    @NotNull
    private BigDecimal initialBalance;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}