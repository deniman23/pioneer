package org.example.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "phone_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "phone"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhoneData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone", length = 13, nullable = false)
    @Pattern(regexp = "^\\+?[0-9]{7,15}$") @NotBlank
    private String phone;

    @Column(name = "primary_flag", nullable = false)
    private boolean primaryFlag;

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "timestamp with time zone default now()"
    )
    @PastOrPresent
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}