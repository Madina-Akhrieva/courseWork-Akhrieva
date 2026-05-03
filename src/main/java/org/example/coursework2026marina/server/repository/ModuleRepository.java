package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.model.Module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModuleRepository extends AbstractJdbcRepository {
    public ModuleRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public List<Module> findByProgramId(long programId) {
        String sql = "SELECT id, program_id, name, topic, credits FROM modules WHERE program_id = ? ORDER BY name";
        List<Module> modules = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, programId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    modules.add(new Module(
                            resultSet.getLong("id"),
                            resultSet.getLong("program_id"),
                            resultSet.getString("name"),
                            resultSet.getString("topic"),
                            resultSet.getInt("credits")
                    ));
                }
            }
        } catch (SQLException ignored) {
        }
        return modules;
    }

    public Module createModule(long programId, String name, String topic, int credits) {
        String sql = "INSERT INTO modules(program_id, name, topic, credits) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, programId);
            statement.setString(2, name);
            statement.setString(3, topic);
            statement.setInt(4, credits);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Module(resultSet.getLong(1), programId, name, topic, credits);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Module creation failed", exception);
        }
        throw new IllegalStateException("Module creation failed");
    }
}
