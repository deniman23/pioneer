package org.example.pioneer.service;

import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(5L)
                .login("alice")
                .passwordHash("hashpwd")
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    // getById

    @Test
    void getById_found() {
        when(userRepo.findById(5L)).thenReturn(Optional.of(user));

        User out = service.getById(5L);

        assertSame(user, out);
        verify(userRepo).findById(5L);
    }

    @Test
    void getById_notFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getById(99L));
        verify(userRepo).findById(99L);
    }

    // search

    @SuppressWarnings("unchecked")
    @Test
    void search_noFilters() {
        int page = 1, size = 2;
        PageRequest pg = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> stubPage = new PageImpl<>(of(user), pg, 1);

        when(userRepo.findAll((Specification<User>) isNull(), eq(pg)))
                .thenReturn(stubPage);

        Page<User> result = service.search(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                page, size
        );

        assertSame(stubPage, result);
        verify(userRepo).findAll((Specification<User>) isNull(), eq(pg));
    }

    @SuppressWarnings("unchecked")
    @Test
    void search_withDateAndName() {
        int page = 0, size = 3;
        LocalDate dobAfter = LocalDate.of(2000, 1, 1);
        String namePrefix = "Al";

        PageRequest pg = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> stubPage = new PageImpl<>(of(user), pg, 1);

        ArgumentCaptor<Specification<User>> specCap =
                ArgumentCaptor.forClass((Class) Specification.class);

        when(userRepo.findAll(specCap.capture(), eq(pg)))
                .thenReturn(stubPage);

        Page<User> result = service.search(
                Optional.of(dobAfter),
                Optional.of(namePrefix),
                Optional.empty(),
                Optional.empty(),
                page, size
        );

        assertSame(stubPage, result);
        verify(userRepo).findAll(any(Specification.class), eq(pg));
        assertNotNull(specCap.getValue(), "Specification should be built");
    }

    // updateName

    @Test
    void updateName_happyPath() {
        when(userRepo.findById(5L)).thenReturn(Optional.of(user));

        service.updateName(5L, "Bob");

        assertEquals("Bob", user.getName());
        verify(userRepo).save(user);
    }

    // updatePassword

    @Test
    void updatePassword_happyPath() {
        when(userRepo.findById(5L)).thenReturn(Optional.of(user));

        service.updatePassword(5L, "newpassw");

        assertEquals("newpassw", user.getPasswordHash());
        verify(userRepo).save(user);
    }

    @Test
    void updatePassword_tooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updatePassword(5L, "short"));
        verify(userRepo, never()).save(any());
    }

    // updateDateOfBirth

    @Test
    void updateDateOfBirth_happyPath() {
        LocalDate newDob = LocalDate.of(1985, 5, 5);
        when(userRepo.findById(5L)).thenReturn(Optional.of(user));

        service.updateDateOfBirth(5L, newDob);

        assertEquals(newDob, user.getDateOfBirth());
        verify(userRepo).save(user);
    }

    @Test
    void updateDateOfBirth_inFuture() {
        LocalDate future = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class,
                () -> service.updateDateOfBirth(5L, future));
        verify(userRepo, never()).save(any());
    }
}