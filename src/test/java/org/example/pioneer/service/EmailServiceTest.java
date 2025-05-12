package org.example.pioneer.service;

import org.example.pioneer.exception.EmailAlreadyExistsException;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.EmailData;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.EmailDataRepository;
import org.example.pioneer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailDataRepository emailRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private EmailService service;

    private final Long userId = 42L;
    private User user;
    private EmailData e1, e2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .login("alice")
                .passwordHash("hash")
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        e1 = EmailData.builder()
                .id(10L)
                .email("one@example.com")
                .primaryFlag(true)
                .user(user)
                .build();

        e2 = EmailData.builder()
                .id(20L)
                .email("two@example.com")
                .primaryFlag(false)
                .user(user)
                .build();
    }

    // getAllForUser

    @Test
    void getAllForUser_happyPath() {
        when(userRepo.existsById(userId)).thenReturn(true);
        when(emailRepo.findAllByUserId(userId)).thenReturn(of(e1, e2));

        List<EmailData> list = service.getAllForUser(userId);

        assertEquals(2, list.size());
        assertTrue(list.contains(e1));
        assertTrue(list.contains(e2));
        verify(emailRepo).findAllByUserId(userId);
    }

    @Test
    void getAllForUser_userNotFound() {
        when(userRepo.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> service.getAllForUser(userId));
        verify(emailRepo, never()).findAllByUserId(any());
    }

    // addEmail

    @Test
    void addEmail_happyPath() {
        String newEmail = "new@example.com";

        when(userRepo.existsById(userId)).thenReturn(true);
        when(emailRepo.existsByEmail(newEmail)).thenReturn(false);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(emailRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmailData result = service.addEmail(userId, newEmail);

        assertEquals(newEmail, result.getEmail());
        assertFalse(result.isPrimaryFlag());
        assertEquals(user, result.getUser());
        verify(emailRepo).save(any(EmailData.class));
    }

    @Test
    void addEmail_userNotFoundByExistsCheck() {
        when(userRepo.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> service.addEmail(userId, "x@x.com"));
        verify(emailRepo, never()).existsByEmail(any());
        verify(emailRepo, never()).save(any());
    }

    @Test
    void addEmail_emailAlreadyExists() {
        when(userRepo.existsById(userId)).thenReturn(true);
        when(emailRepo.existsByEmail(e1.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> service.addEmail(userId, e1.getEmail()));
        verify(emailRepo, never()).save(any());
    }

    // updateEmail

    @Test
    void updateEmail_happyPath() {
        Long emailId = e1.getId();
        String updatedEmail = "upd@example.com";

        when(emailRepo.findById(emailId)).thenReturn(Optional.of(e1));
        when(emailRepo.existsByEmail(updatedEmail)).thenReturn(false);
        when(emailRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmailData out = service.updateEmail(userId, emailId, updatedEmail);

        assertEquals(updatedEmail, out.getEmail());
        verify(emailRepo).save(e1);
    }

    @Test
    void updateEmail_notFound() {
        when(emailRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateEmail(userId, 999L, "x@x.com"));
    }

    @Test
    void updateEmail_wrongUser() {
        EmailData other = EmailData.builder()
                .id(50L)
                .email("z@z.com")
                .primaryFlag(false)
                .user(User.builder()
                        .id(99L)
                        .login("bob")
                        .passwordHash("p")
                        .name("Bob")
                        .dateOfBirth(LocalDate.of(1980,1,1))
                        .build())
                .build();
        when(emailRepo.findById(50L)).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateEmail(userId, 50L, "new@x.com"));
    }

    @Test
    void updateEmail_newEmailExists() {
        when(emailRepo.findById(e2.getId())).thenReturn(Optional.of(e2));
        when(emailRepo.existsByEmail(e2.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> service.updateEmail(userId, e2.getId(), e2.getEmail()));
    }

    // deleteEmail

    @Test
    void deleteEmail_happyPath() {
        when(emailRepo.findById(e1.getId())).thenReturn(Optional.of(e1));
        when(emailRepo.findAllByUserId(userId)).thenReturn(of(e1, e2));

        service.deleteEmail(userId, e1.getId());

        verify(emailRepo).deleteById(e1.getId());
    }

    @Test
    void deleteEmail_notFound() {
        when(emailRepo.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteEmail(userId, 123L));
    }

    @Test
    void deleteEmail_wrongUser() {
        EmailData other = EmailData.builder()
                .id(5L)
                .email("x@y.com")
                .primaryFlag(false)
                .user(User.builder()
                        .id(7L)
                        .login("c")
                        .passwordHash("p")
                        .name("C")
                        .dateOfBirth(LocalDate.of(1970,1,1))
                        .build())
                .build();
        when(emailRepo.findById(5L)).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteEmail(userId, 5L));
    }

    @Test
    void deleteEmail_lastEmailThrows() {
        when(emailRepo.findById(e1.getId())).thenReturn(Optional.of(e1));
        when(emailRepo.findAllByUserId(userId)).thenReturn(of(e1));

        assertThrows(IllegalStateException.class,
                () -> service.deleteEmail(userId, e1.getId()));
        verify(emailRepo, never()).deleteById(any());
    }
}