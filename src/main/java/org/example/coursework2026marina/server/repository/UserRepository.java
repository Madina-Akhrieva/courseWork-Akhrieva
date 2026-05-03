package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.common.Role;
import org.example.coursework2026marina.server.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);

    User createUser(String username, String fullName, String passwordHash, Role role);

    void updatePassword(long userId, String passwordHash);

    List<Map<String, Object>> findAllStudents();
}
