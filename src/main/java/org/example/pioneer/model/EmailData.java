package org.example.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "email_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "email"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 200, nullable = false)
    @Email @NotBlank
    private String email;

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