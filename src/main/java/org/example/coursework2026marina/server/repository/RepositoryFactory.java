package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;

public class RepositoryFactory {
    private final DatabaseManager databaseManager;

    public RepositoryFactory(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public UserRepository userRepository() {
        return new JdbcUserRepository(databaseManager);
    }

    public AnalyticsRepository analyticsRepository() {
        return new AnalyticsRepository(databaseManager);
    }

    public ProgramRepository programRepository() {
        return new ProgramRepository(databaseManager);
    }

    public ModuleRepository moduleRepository() {
        return new ModuleRepository(databaseManager);
    }

    public GradeRepository gradeRepository() {
        return new GradeRepository(databaseManager);
    }

    public EnrollmentRepository enrollmentRepository() {
        return new EnrollmentRepository(databaseManager);
    }

    public AssignmentRepository assignmentRepository() {
        return new AssignmentRepository(databaseManager);
    }

    public SubmissionRepository submissionRepository() {
        return new SubmissionRepository(databaseManager);
    }
}
