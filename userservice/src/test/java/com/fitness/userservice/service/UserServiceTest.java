package com.fitness.userservice.service;

import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Create a fresh mock for each test
        repository = mock(UserRepository.class);
        // Initialize service with mock repository
        userService = new UserService(repository);
    }

    @Test
    void existByUserId_returnsFalse_forNullOrBlank() {
        // No need to mock repository for null/blank checks as they're handled in service
        assertAll(
            () -> assertFalse(userService.existByUserId(null), "should be false for null"),
            () -> assertFalse(userService.existByUserId(""), "should be false for empty string"),
            () -> assertFalse(userService.existByUserId("   "), "should be false for blank string")
        );

        // Verify repository was never called
        verify(repository, never()).existsByKeycloakId(any());
    }

    @Test
    void existByUserId_returnsTrue_whenUserExists() {
        String keycloakId = "test-keycloak-id";
        when(repository.existsByKeycloakId(keycloakId)).thenReturn(true);

        boolean result = userService.existByUserId(keycloakId);

        assertTrue(result, "should return true when user exists");
        verify(repository).existsByKeycloakId(keycloakId);
    }

    @Test
    void existByUserId_returnsFalse_whenUserNotFound() {
        String keycloakId = "nonexistent-id";
        when(repository.existsByKeycloakId(keycloakId)).thenReturn(false);

        boolean result = userService.existByUserId(keycloakId);

        assertFalse(result, "should return false when user doesn't exist");
        verify(repository).existsByKeycloakId(keycloakId);
    }

    @Test
    void getUserProfile_returnsUserResponse_whenUserExists() {
        // Arrange
        String userId = "test-user-id";
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");

        when(repository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

        // Act
        var response = userService.getUserProfile(userId);

        // Assert
        assertNotNull(response, "response should not be null");
        assertAll(
            () -> assertEquals(userId, response.getId(), "ID should match"),
            () -> assertEquals("test@example.com", response.getEmail(), "email should match"),
            () -> assertEquals("Test", response.getFirstName(), "firstName should match"),
            () -> assertEquals("User", response.getLastName(), "lastName should match")
        );
        verify(repository).findById(userId);
    }

    @Test
    void getUserProfile_throwsException_whenUserNotFound() {
        String userId = "nonexistent-id";
        when(repository.findById(userId)).thenReturn(java.util.Optional.empty());

        var exception = assertThrows(
            RuntimeException.class,
            () -> userService.getUserProfile(userId),
            "should throw RuntimeException when user not found"
        );

        assertEquals("User Not Found", exception.getMessage());
        verify(repository).findById(userId);
    }
}
