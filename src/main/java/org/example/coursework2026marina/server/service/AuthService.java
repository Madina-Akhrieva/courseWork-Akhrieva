package org.example.coursework2026marina.server.service;

import org.example.coursework2026marina.common.Role;
import org.example.coursework2026marina.server.model.User;
import org.example.coursework2026marina.server.repository.UserRepository;
import org.example.coursework2026marina.server.security.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> sessionStore = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String fullName, String password, Role role) {
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new IllegalArgumentException("Пользователь уже существует");
        });
        String encoded = passwordEncoder.encode(password);
        return userRepository.createUser(username, fullName, encoded, role);
    }

    public String login(String username, String password) {
        Optional<User> candidate = userRepository.findByUsername(username);
        if (!candidate.isPresent()) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        User user = candidate.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        String token = UUID.randomUUID().toString();
        sessionStore.put(token, user);
        return token;
    }

    public User requireUser(String token) {
        User user = sessionStore.get(token);
        if (user == null) {
            throw new IllegalArgumentException("Неавторизованный запрос");
        }
        return user;
    }

    public void changePassword(String token, String oldPassword, String newPassword) {
        User user = requireUser(token);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Старый пароль неверен");
        }
        String newEncoded = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(user.getId(), newEncoded);
        user.setPasswordHash(newEncoded);
    }
}
