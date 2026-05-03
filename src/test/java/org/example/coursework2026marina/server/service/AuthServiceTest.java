package org.example.coursework2026marina.server.service;

import org.example.coursework2026marina.common.Role;
import org.example.coursework2026marina.server.model.User;
import org.example.coursework2026marina.server.repository.UserRepository;
import org.example.coursework2026marina.server.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    public void testRegisterNewUser() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encrypted");
        when(userRepository.createUser("newuser", "New User", "encrypted", Role.STUDENT))
                .thenReturn(new User(1, "newuser", "New User", "encrypted", Role.STUDENT));

        User result = authService.register("newuser", "New User", "password123", Role.STUDENT);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userRepository, times(1)).createUser(anyString(), anyString(), anyString(), any(Role.class));
    }

    @Test
    public void testRegisterExistingUserThrows() {
        when(userRepository.findByUsername("existing"))
                .thenReturn(Optional.of(new User(1, "existing", "Existing User", "hash", Role.STUDENT)));

        assertThrows(IllegalArgumentException.class, () ->
                authService.register("existing", "Existing User", "password123", Role.STUDENT)
        );
    }

    @Test
    public void testLoginSuccess() {
        User user = new User(1, "testuser", "Test User", "encrypted", Role.STUDENT);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encrypted")).thenReturn(true);

        String token = authService.login("testuser", "password123");

        assertNotNull(token);
        User sessionUser = authService.requireUser(token);
        assertEquals("testuser", sessionUser.getUsername());
    }

    @Test
    public void testLoginInvalidPassword() {
        User user = new User(1, "testuser", "Test User", "encrypted", Role.STUDENT);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encrypted")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                authService.login("testuser", "wrongpassword")
        );
    }

    @Test
    public void testLoginNonExistentUser() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                authService.login("nonexistent", "password123")
        );
    }

    @Test
    public void testChangePassword() {
        User user = new User(1, "testuser", "Test User", "oldencrypted", Role.STUDENT);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpassword", "oldencrypted")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("newencrypted");

        String token = authService.login("testuser", "oldpassword");
        authService.changePassword(token, "oldpassword", "newpassword");

        verify(userRepository, times(1)).updatePassword(1, "newencrypted");
    }
}
