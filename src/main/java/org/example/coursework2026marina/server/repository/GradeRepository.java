package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.model.StudentGrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeRepository extends AbstractJdbcRepository {
    public GradeRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void recordGrade(long studentId, long moduleId, double score) {
        String sql = "INSERT INTO student_grades(student_id, module_id, score, recorded_at) VALUES (?, ?, ?, NOW())";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            statement.setLong(2, moduleId);
            statement.setDouble(3, score);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Grade recording failed", exception);
        }
    }

    public List<StudentGrade> findByStudentId(long studentId) {
        String sql = "SELECT id, student_id, module_id, score, recorded_at FROM student_grades WHERE student_id = ? ORDER BY recorded_at DESC";
        List<StudentGrade> grades = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    grades.add(new StudentGrade(
                            resultSet.getLong("id"),
                            resultSet.getLong("student_id"),
                            resultSet.getLong("module_id"),
                            resultSet.getDouble("score"),
                            resultSet.getString("recorded_at")
                    ));
                }
            }
        } catch (SQLException ignored) {
        }
        return grades;
    }
}
