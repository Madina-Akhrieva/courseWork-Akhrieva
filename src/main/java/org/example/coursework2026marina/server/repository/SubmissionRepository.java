package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionRepository extends AbstractJdbcRepository {
    public SubmissionRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void upsertSubmission(long assignmentId, long studentId, String fileName, byte[] fileData, String comment) {
        String sql = "INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (assignment_id, student_id) DO UPDATE SET " +
                "file_name = EXCLUDED.file_name, file_data = EXCLUDED.file_data, comment = EXCLUDED.comment, submitted_at = NOW()";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, assignmentId);
            statement.setLong(2, studentId);
            statement.setString(3, fileName);
            statement.setBytes(4, fileData);
            statement.setString(5, comment);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Submission failed", exception);
        }
    }

    public List<Map<String, Object>> findByStudentId(long studentId) {
        String sql = "SELECT s.id, s.assignment_id, a.title assignment_title, s.file_name, s.comment, s.submitted_at " +
                "FROM assignment_submissions s " +
                "JOIN course_assignments a ON a.id = s.assignment_id " +
                "WHERE s.student_id = ? ORDER BY s.submitted_at DESC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", resultSet.getLong("id"));
                    row.put("assignmentId", resultSet.getLong("assignment_id"));
                    row.put("assignmentTitle", resultSet.getString("assignment_title"));
                    row.put("fileName", resultSet.getString("file_name"));
                    row.put("comment", resultSet.getString("comment"));
                    row.put("submittedAt", String.valueOf(resultSet.getTimestamp("submitted_at")));
                    rows.add(row);
                }
            }
        } catch (SQLException ignored) {
        }
        return rows;
    }

    public List<Map<String, Object>> findForAdmin(Long assignmentId) {
        String sql = "SELECT s.id, s.assignment_id, a.title assignment_title, u.full_name student_name, u.username student_username, " +
                "s.file_name, s.comment, OCTET_LENGTH(s.file_data) file_size, s.submitted_at " +
                "FROM assignment_submissions s " +
                "JOIN course_assignments a ON a.id = s.assignment_id " +
                "JOIN users u ON u.id = s.student_id " +
                "WHERE (? IS NULL OR s.assignment_id = ?) " +
                "ORDER BY s.submitted_at DESC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (assignmentId == null) {
                statement.setNull(1, java.sql.Types.BIGINT);
                statement.setNull(2, java.sql.Types.BIGINT);
            } else {
                statement.setLong(1, assignmentId);
                statement.setLong(2, assignmentId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", resultSet.getLong("id"));
                    row.put("assignmentId", resultSet.getLong("assignment_id"));
                    row.put("assignmentTitle", resultSet.getString("assignment_title"));
                    row.put("studentName", resultSet.getString("student_name"));
                    row.put("studentUsername", resultSet.getString("student_username"));
                    row.put("fileName", resultSet.getString("file_name"));
                    row.put("comment", resultSet.getString("comment"));
                    row.put("fileSize", resultSet.getLong("file_size"));
                    row.put("submittedAt", String.valueOf(resultSet.getTimestamp("submitted_at")));
                    rows.add(row);
                }
            }
        } catch (SQLException ignored) {
        }
        return rows;
    }
}
