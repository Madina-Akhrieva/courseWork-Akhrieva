package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.common.Role;
import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcUserRepository extends AbstractJdbcRepository implements UserRepository {
    public JdbcUserRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT u.id, u.username, u.full_name, u.password_hash, r.name role_name " +
                "FROM users u JOIN roles r ON r.id = u.role_id WHERE u.username = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("full_name"),
                            resultSet.getString("password_hash"),
                            Role.valueOf(resultSet.getString("role_name"))
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public User createUser(String username, String fullName, String passwordHash, Role role) {
        String sql = "INSERT INTO users(username, full_name, password_hash, role_id) " +
                "VALUES (?, ?, ?, (SELECT id FROM roles WHERE name = ?)) RETURNING id";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, fullName);
            statement.setString(3, passwordHash);
            statement.setString(4, role.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(resultSet.getLong(1), username, fullName, passwordHash, role);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("User creation failed", exception);
        }
        throw new IllegalStateException("User creation failed");
    }

    @Override
    public void updatePassword(long userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Password update failed", exception);
        }
    }

    @Override
    public List<Map<String, Object>> findAllStudents() {
        String sql = "SELECT u.id, u.username, u.full_name, COUNT(e.program_id) enrolled_programs " +
                "FROM users u " +
                "JOIN roles r ON r.id = u.role_id " +
                "LEFT JOIN enrollments e ON e.student_id = u.id " +
                "WHERE r.name = 'STUDENT' " +
                "GROUP BY u.id, u.username, u.full_name " +
                "ORDER BY u.full_name";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", resultSet.getLong("id"));
                row.put("username", resultSet.getString("username"));
                row.put("fullName", resultSet.getString("full_name"));
                row.put("enrolledPrograms", resultSet.getLong("enrolled_programs"));
                rows.add(row);
            }
        } catch (SQLException ignored) {
        }
        return rows;
    }
}
