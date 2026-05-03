package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.model.EducationalProgram;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgramRepository extends AbstractJdbcRepository {
    public ProgramRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public List<EducationalProgram> findAll() {
        String sql = "SELECT id, name, description, duration, max_students FROM educational_programs ORDER BY name";
        List<EducationalProgram> programs = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                programs.add(new EducationalProgram(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("duration"),
                        resultSet.getInt("max_students")
                ));
            }
        } catch (SQLException ignored) {
        }
        return programs;
    }

    public EducationalProgram createProgram(String name, String description, int duration, int maxStudents) {
        String sql = "INSERT INTO educational_programs(name, description, duration, max_students) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, duration);
            statement.setInt(4, maxStudents);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new EducationalProgram(resultSet.getLong(1), name, description, duration, maxStudents);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Program creation failed", exception);
        }
        throw new IllegalStateException("Program creation failed");
    }
}
