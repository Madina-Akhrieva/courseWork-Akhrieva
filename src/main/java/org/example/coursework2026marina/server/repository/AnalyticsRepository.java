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

public class AnalyticsRepository extends AbstractJdbcRepository {
    public AnalyticsRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public List<Map<String, Object>> getProgramEffectiveness() {
        String sql = "SELECT p.name program_name, ROUND(AVG(sg.score)::numeric, 2) avg_score, COUNT(DISTINCT e.student_id) students_count " +
                "FROM educational_programs p " +
                "LEFT JOIN enrollments e ON e.program_id = p.id " +
            "LEFT JOIN modules m ON m.program_id = p.id " +
            "LEFT JOIN student_grades sg ON sg.module_id = m.id AND sg.student_id = e.student_id " +
                "GROUP BY p.name ORDER BY avg_score DESC NULLS LAST";
        return selectAsMapList(sql);
    }

    public List<Map<String, Object>> getRiskStudents(double threshold) {
        String sql = "SELECT u.full_name student_name, ROUND(AVG(sg.score)::numeric, 2) avg_score " +
                "FROM users u " +
            "JOIN student_grades sg ON sg.student_id = u.id " +
            "GROUP BY u.full_name HAVING AVG(sg.score) < ? ORDER BY avg_score";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, threshold);
            try (ResultSet resultSet = statement.executeQuery()) {
                return collectRows(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Risk students query failed", exception);
        }
    }

    public List<Map<String, Object>> getTopModules() {
        String sql = "SELECT m.name module_name, ROUND(AVG(sg.score)::numeric, 2) avg_score " +
            "FROM modules m JOIN student_grades sg ON sg.module_id = m.id " +
                "GROUP BY m.name ORDER BY avg_score DESC LIMIT 5";
        return selectAsMapList(sql);
    }

    public List<Map<String, Object>> getStudentProgress(String username) {
        String sql = "SELECT m.name module_name, sg.score, sg.recorded_at " +
            "FROM student_grades sg " +
            "JOIN modules m ON m.id = sg.module_id " +
            "JOIN users u ON u.id = sg.student_id " +
            "WHERE u.username = ? ORDER BY sg.recorded_at DESC";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return collectRows(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Student progress query failed", exception);
        }
    }

    private List<Map<String, Object>> selectAsMapList(String sql) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return collectRows(resultSet);
        } catch (SQLException exception) {
            throw new IllegalStateException("Analytics query failed", exception);
        }
    }

    private List<Map<String, Object>> collectRows(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        int count = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int index = 1; index <= count; index++) {
                row.put(resultSet.getMetaData().getColumnLabel(index), resultSet.getObject(index));
            }
            rows.add(row);
        }
        return rows;
    }
}
