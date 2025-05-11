package org.example.pioneer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "login")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", length = 255, nullable = false, unique = true)
    @NotBlank
    private String login;

    @Column(name = "password_hash", length = 500, nullable = false)
    @NotBlank
    private String passwordHash;

    @Column(name = "name", length = 500, nullable = false)
    @NotBlank
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    @Past
    private LocalDate dateOfBirth;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<PhoneData> phones = new HashSet<>();

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false,
            fetch = FetchType.LAZY
    )
    @PrimaryKeyJoinColumn(name = "id")
    private Account account;

    // утилиты удобного управления коллекциями
    public void addEmail(EmailData email) {
        emails.add(email);
        email.setUser(this);
    }
    public void removeEmail(EmailData email) {
        emails.remove(email);
        email.setUser(null);
    }
    public void addPhone(PhoneData phone) {
        phones.add(phone);
        phone.setUser(this);
    }
    public void removePhone(PhoneData phone) {
        phones.remove(phone);
        phone.setUser(null);
    }
}