package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EnrollmentRepository extends AbstractJdbcRepository {
    public EnrollmentRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public boolean enrollStudentInProgram(long studentId, long programId) {
        String sql = "INSERT INTO enrollments(student_id, program_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            statement.setLong(2, programId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Enrollment failed", exception);
        }
    }
}
