package com.fitness.userservice.service;

import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        userService = new UserService(repository);
    }

    @Test
    void existByUserId_returnsFalse_forNullOrBlank() {
        assertFalse(userService.existByUserId(null));
        assertFalse(userService.existByUserId(""));
        assertFalse(userService.existByUserId("   "));
    }

    @Test
    void existByUserId_returnsTrue_whenExistsById() {
        when(repository.existsById("internal-id")).thenReturn(true);

        boolean result = userService.existByUserId("internal-id");

        assertTrue(result);
        verify(repository, times(1)).existsById("internal-id");
//        verify(repository, never()).findByKeycloakId(anyString());
    }

    @Test
    void existByUserId_returnsTrue_whenFoundByKeycloakId() {
        when(repository.existsById("kc-123")).thenReturn(false);
        User u = new User();
        u.setId("1");
        u.setKeycloakId("kc-123");
//        when(repository.findByKeycloakId("kc-123")).thenReturn(u);

        boolean result = userService.existByUserId("kc-123");

        assertTrue(result);
        verify(repository, times(1)).existsById("kc-123");
//        verify(repository, times(1)).findByKeycloakId("kc-123");
    }

    @Test
    void existByUserId_returnsFalse_whenNotFound() {
        when(repository.existsById("not-found")).thenReturn(false);
//        when(repository.findByKeycloakId("not-found")).thenReturn(null);

        boolean result = userService.existByUserId("not-found");

        assertFalse(result);
    }
}

