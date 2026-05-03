package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.model.Assignment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssignmentRepository extends AbstractJdbcRepository {
    public AssignmentRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public Assignment createAssignment(long programId, String title, String description, String dueDate) {
        String sql = "INSERT INTO course_assignments(program_id, title, description, due_date) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, programId);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setDate(4, Date.valueOf(dueDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Assignment(resultSet.getLong(1), programId, title, description, dueDate);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Assignment creation failed", exception);
        }
        throw new IllegalStateException("Assignment creation failed");
    }

    public List<Assignment> findByProgramId(long programId) {
        String sql = "SELECT id, program_id, title, description, due_date FROM course_assignments WHERE program_id = ? ORDER BY due_date, id";
        List<Assignment> assignments = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, programId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    assignments.add(new Assignment(
                            resultSet.getLong("id"),
                            resultSet.getLong("program_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            String.valueOf(resultSet.getDate("due_date"))
                    ));
                }
            }
        } catch (SQLException ignored) {
        }
        return assignments;
    }
}
