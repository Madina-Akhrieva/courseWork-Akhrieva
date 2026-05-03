package org.example.coursework2026marina.server.service;

import org.example.coursework2026marina.server.model.Assignment;
import org.example.coursework2026marina.server.model.EducationalProgram;
import org.example.coursework2026marina.server.model.Module;
import org.example.coursework2026marina.server.repository.AssignmentRepository;
import org.example.coursework2026marina.server.repository.GradeRepository;
import org.example.coursework2026marina.server.repository.ModuleRepository;
import org.example.coursework2026marina.server.repository.ProgramRepository;
import org.example.coursework2026marina.server.repository.SubmissionRepository;
import org.example.coursework2026marina.server.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class AdminService {
    private final ProgramRepository programRepository;
    private final ModuleRepository moduleRepository;
    private final GradeRepository gradeRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public AdminService(ProgramRepository programRepository,
                        ModuleRepository moduleRepository,
                        GradeRepository gradeRepository,
                        AssignmentRepository assignmentRepository,
                        SubmissionRepository submissionRepository,
                        UserRepository userRepository) {
        this.programRepository = programRepository;
        this.moduleRepository = moduleRepository;
        this.gradeRepository = gradeRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    public EducationalProgram createProgram(String name, String description, int duration, int maxStudents) {
        return programRepository.createProgram(name, description, duration, maxStudents);
    }

    public Module createModule(long programId, String name, String topic, int credits) {
        return moduleRepository.createModule(programId, name, topic, credits);
    }

    public void recordStudentGrade(long studentId, long moduleId, double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Оценка должна быть от 0 до 100");
        }
        gradeRepository.recordGrade(studentId, moduleId, score);
    }

    public List<Module> getProgramModules(long programId) {
        return moduleRepository.findByProgramId(programId);
    }

    public Assignment createAssignment(long programId, String title, String description, String dueDate) {
        if (programId <= 0) {
            throw new IllegalArgumentException("Неверный ID программы");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Введите название задания");
        }
        return assignmentRepository.createAssignment(programId, title.trim(), description, dueDate);
    }

    public List<Map<String, Object>> getAssignmentSubmissions(Long assignmentId) {
        return submissionRepository.findForAdmin(assignmentId);
    }

    public List<Map<String, Object>> getStudentList() {
        return userRepository.findAllStudents();
    }
}
