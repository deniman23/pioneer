package org.example.pioneer.service;

import org.example.pioneer.exception.PhoneAlreadyExistsException;
import org.example.pioneer.exception.ResourceNotFoundException;
import org.example.pioneer.model.PhoneData;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.PhoneDataRepository;
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
class PhoneDataServiceTest {

    @Mock
    private PhoneDataRepository phoneRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private PhoneDataService service;

    private final Long userId = 100L;
    private User user;
    private PhoneData pd1, pd2;

    @BeforeEach
    void setUp() {
        // строим тестового User-а
        user = User.builder()
                .id(userId)
                .login("testLogin")
                .passwordHash("hash")
                .name("Test User")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        // два PhoneData для тестов
        pd1 = PhoneData.builder()
                .id(10L)
                .phone("111")
                .primaryFlag(false)
                .user(user)
                .build();

        pd2 = PhoneData.builder()
                .id(20L)
                .phone("222")
                .primaryFlag(true)
                .user(user)
                .build();
    }

    // getAllForUser

    @Test
    void getAllForUser_happyPath() {
        when(userRepo.existsById(userId)).thenReturn(true);
        when(phoneRepo.findAllByUserId(userId)).thenReturn(of(pd1, pd2));

        List<PhoneData> list = service.getAllForUser(userId);

        assertEquals(2, list.size());
        assertTrue(list.contains(pd1));
        assertTrue(list.contains(pd2));
        verify(phoneRepo).findAllByUserId(userId);
    }

    @Test
    void getAllForUser_userNotFound() {
        when(userRepo.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> service.getAllForUser(userId));
        verify(phoneRepo, never()).findAllByUserId(any());
    }

    // addPhone

    @Test
    void addPhone_happyPath() {
        String newPhone = "333";
        when(userRepo.existsById(userId)).thenReturn(true);
        when(phoneRepo.existsByPhone(newPhone)).thenReturn(false);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(phoneRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PhoneData result = service.addPhone(userId, newPhone);

        assertEquals(newPhone, result.getPhone());
        assertEquals(user, result.getUser());
        verify(phoneRepo).save(any(PhoneData.class));
    }

    @Test
    void addPhone_userNotFoundByExistsCheck() {
        when(userRepo.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> service.addPhone(userId, "333"));
        verify(phoneRepo, never()).existsByPhone(any());
        verify(phoneRepo, never()).save(any());
    }

    @Test
    void addPhone_phoneAlreadyExists() {
        when(userRepo.existsById(userId)).thenReturn(true);
        when(phoneRepo.existsByPhone(pd1.getPhone())).thenReturn(true);
        assertThrows(PhoneAlreadyExistsException.class,
                () -> service.addPhone(userId, pd1.getPhone()));
        verify(phoneRepo, never()).save(any());
    }

    // updatePhone

    @Test
    void updatePhone_happyPath() {
        Long phoneId = pd1.getId();
        String newNumber = "999";

        when(phoneRepo.findById(phoneId)).thenReturn(Optional.of(pd1));
        when(phoneRepo.existsByPhone(newNumber)).thenReturn(false);
        when(phoneRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PhoneData updated = service.updatePhone(userId, phoneId, newNumber);

        assertEquals(newNumber, updated.getPhone());
        verify(phoneRepo).save(pd1);
    }

    @Test
    void updatePhone_notFound() {
        when(phoneRepo.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updatePhone(userId, 123L, "000"));
    }

    @Test
    void updatePhone_wrongUser() {
        PhoneData other = PhoneData.builder()
                .id(50L)
                .phone("555")
                .primaryFlag(false)
                .user(User.builder()
                        .id(2L)
                        .login("x")
                        .passwordHash("x")
                        .name("x")
                        .dateOfBirth(LocalDate.of(1990,1,1))
                        .build())
                .build();
        when(phoneRepo.findById(50L)).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> service.updatePhone(userId, 50L, "123"));
    }

    @Test
    void updatePhone_newPhoneExists() {
        when(phoneRepo.findById(pd2.getId())).thenReturn(Optional.of(pd2));
        when(phoneRepo.existsByPhone(pd2.getPhone())).thenReturn(true);

        assertThrows(PhoneAlreadyExistsException.class,
                () -> service.updatePhone(userId, pd2.getId(), pd2.getPhone()));
    }

    // deletePhone

    @Test
    void deletePhone_happyPath() {
        when(phoneRepo.findById(pd1.getId())).thenReturn(Optional.of(pd1));
        when(phoneRepo.findAllByUserId(userId)).thenReturn(of(pd1, pd2));

        service.deletePhone(userId, pd1.getId());

        verify(phoneRepo).deleteById(pd1.getId());
    }

    @Test
    void deletePhone_notFound() {
        when(phoneRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.deletePhone(userId, 999L));
    }

    @Test
    void deletePhone_wrongUser() {
        PhoneData other = PhoneData.builder()
                .id(5L)
                .phone("000")
                .primaryFlag(false)
                .user(User.builder()
                        .id(2L)
                        .login("y")
                        .passwordHash("y")
                        .name("Y")
                        .dateOfBirth(LocalDate.of(1990,1,1))
                        .build())
                .build();
        when(phoneRepo.findById(5L)).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> service.deletePhone(userId, 5L));
    }

    @Test
    void deletePhone_lastPhoneThrows() {
        when(phoneRepo.findById(pd1.getId())).thenReturn(Optional.of(pd1));
        when(phoneRepo.findAllByUserId(userId)).thenReturn(of(pd1));

        assertThrows(IllegalStateException.class,
                () -> service.deletePhone(userId, pd1.getId()));
        verify(phoneRepo, never()).deleteById(any());
    }
}