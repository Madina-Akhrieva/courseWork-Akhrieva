package org.example.coursework2026marina.server.service;

import org.example.coursework2026marina.server.model.Assignment;
import org.example.coursework2026marina.server.model.EducationalProgram;
import org.example.coursework2026marina.server.model.StudentGrade;
import org.example.coursework2026marina.server.repository.AssignmentRepository;
import org.example.coursework2026marina.server.repository.EnrollmentRepository;
import org.example.coursework2026marina.server.repository.GradeRepository;
import org.example.coursework2026marina.server.repository.ProgramRepository;
import org.example.coursework2026marina.server.repository.SubmissionRepository;

import java.util.List;
import java.util.Map;

public class StudentService {
    private final ProgramRepository programRepository;
    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    public StudentService(ProgramRepository programRepository,
                          GradeRepository gradeRepository,
                          EnrollmentRepository enrollmentRepository,
                          AssignmentRepository assignmentRepository,
                          SubmissionRepository submissionRepository) {
        this.programRepository = programRepository;
        this.gradeRepository = gradeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    public List<EducationalProgram> getAvailablePrograms() {
        return programRepository.findAll();
    }

    public List<StudentGrade> getMyProgress(long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public boolean enrollInProgram(long studentId, long programId) {
        if (programId <= 0) {
            throw new IllegalArgumentException("Не выбрана программа для записи");
        }
        return enrollmentRepository.enrollStudentInProgram(studentId, programId);
    }

    public List<Assignment> getProgramAssignments(long programId) {
        if (programId <= 0) {
            throw new IllegalArgumentException("Не выбрана программа");
        }
        return assignmentRepository.findByProgramId(programId);
    }

    public void submitAssignment(long studentId, long assignmentId, String fileName, byte[] fileData, String comment) {
        if (assignmentId <= 0) {
            throw new IllegalArgumentException("Не выбрано задание");
        }
        if (fileName == null || fileName.trim().isEmpty() || fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException("Файл ответа не выбран");
        }
        submissionRepository.upsertSubmission(assignmentId, studentId, fileName, fileData, comment);
    }

    public List<Map<String, Object>> getMySubmissions(long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
}
