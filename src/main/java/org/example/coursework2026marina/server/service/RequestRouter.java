package org.example.coursework2026marina.server.service;

import org.example.coursework2026marina.common.Command;
import org.example.coursework2026marina.common.Request;
import org.example.coursework2026marina.common.Response;
import org.example.coursework2026marina.common.Role;
import org.example.coursework2026marina.server.model.Assignment;
import org.example.coursework2026marina.server.model.EducationalProgram;
import org.example.coursework2026marina.server.model.Module;
import org.example.coursework2026marina.server.model.User;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestRouter {
    private final AuthService authService;
    private final AdminService adminService;
    private final StudentService studentService;
    private final AnalyticsService analyticsService;

    public RequestRouter(AuthService authService, AdminService adminService,
                         StudentService studentService, AnalyticsService analyticsService) {
        this.authService = authService;
        this.adminService = adminService;
        this.studentService = studentService;
        this.analyticsService = analyticsService;
    }

    public Response route(Request request) {
        try {
            if (request == null || request.getCommand() == null) {
                return Response.fail("Пустой запрос", "EMPTY_REQUEST");
            }
            Command cmd = request.getCommand();
            if (cmd == Command.REGISTER) return handleRegister(request);
            if (cmd == Command.LOGIN) return handleLogin(request);
            if (cmd == Command.GET_USER_PROFILE) return handleProfile(request);
            if (cmd == Command.CHANGE_PASSWORD) return handleChangePassword(request);
            if (cmd == Command.PING) return Response.ok("pong");
            // Student
            if (cmd == Command.GET_MY_PROGRESS) return handleMyProgress(request);
            if (cmd == Command.GET_AVAILABLE_PROGRAMS) return handleAvailablePrograms(request);
            if (cmd == Command.ENROLL_PROGRAM) return handleEnrollProgram(request);
            if (cmd == Command.GET_PROGRAM_ASSIGNMENTS) return handleGetProgramAssignments(request);
            if (cmd == Command.SUBMIT_ASSIGNMENT) return handleSubmitAssignment(request);
            if (cmd == Command.GET_MY_SUBMISSIONS) return handleGetMySubmissions(request);
            // Admin
            if (cmd == Command.CREATE_PROGRAM) return handleCreateProgram(request);
            if (cmd == Command.CREATE_MODULE) return handleCreateModule(request);
            if (cmd == Command.RECORD_STUDENT_GRADE) return handleRecordGrade(request);
            if (cmd == Command.CREATE_ASSIGNMENT) return handleCreateAssignment(request);
            if (cmd == Command.GET_ASSIGNMENT_SUBMISSIONS) return handleGetAssignmentSubmissions(request);
            if (cmd == Command.GET_ALL_ANALYTICS) return handleAllAnalytics(request);
            if (cmd == Command.GET_STUDENT_LIST) return handleStudentList(request);
            return Response.fail("Неизвестная команда", "UNKNOWN_COMMAND");
        } catch (Exception exception) {
            return Response.fail(exception.getMessage(), "SERVER_ERROR");
        }
    }

    // ---- Auth ----

    private Response handleRegister(Request request) {
        Map<String, Object> p = request.getPayload();
        String username = asString(p.get("username"));
        String fullName = asString(p.get("fullName"));
        String password = asString(p.get("password"));
        Role role = Role.valueOf(asString(p.getOrDefault("role", "STUDENT")));
        User user = authService.register(username, fullName, password, role);
        return Response.ok("Регистрация успешна")
                .withData("userId", user.getId())
                .withData("role", user.getRole().name());
    }

    private Response handleLogin(Request request) {
        Map<String, Object> p = request.getPayload();
        String username = asString(p.get("username"));
        String password = asString(p.get("password"));
        String token = authService.login(username, password);
        User user = authService.requireUser(token);
        return Response.ok("Вход выполнен")
                .withData("token", token)
                .withData("fullName", user.getFullName())
                .withData("role", user.getRole().name())
                .withData("username", user.getUsername());
    }

    private Response handleProfile(Request request) {
        User user = authService.requireUser(request.getToken());
        return Response.ok("Профиль")
                .withData("username", user.getUsername())
                .withData("fullName", user.getFullName())
                .withData("role", user.getRole().name());
    }

    private Response handleChangePassword(Request request) {
        Map<String, Object> p = request.getPayload();
        authService.changePassword(request.getToken(),
                asString(p.get("oldPassword")), asString(p.get("newPassword")));
        return Response.ok("Пароль обновлен");
    }

    // ---- Student ----

    private Response handleMyProgress(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.STUDENT);
        List grades = studentService.getMyProgress(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("grades", grades);
        return Response.ok("Успеваемость студента").withData("data", data);
    }

    private Response handleAvailablePrograms(Request request) {
        authService.requireUser(request.getToken());
        List programs = studentService.getAvailablePrograms();
        return Response.ok("Доступные программы").withData("programs", programs);
    }

    private Response handleEnrollProgram(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.STUDENT);
        Map<String, Object> p = request.getPayload();
        long programId = parseLong(p.get("programId"));
        boolean enrolled = studentService.enrollInProgram(user.getId(), programId);
        if (enrolled) {
            return Response.ok("Запись на программу выполнена")
                    .withData("programId", programId).withData("enrolled", true);
        }
        return Response.ok("Вы уже записаны на эту программу")
                .withData("programId", programId).withData("enrolled", false);
    }

    private Response handleGetProgramAssignments(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.STUDENT);
        long programId = parseLong(request.getPayload().get("programId"));
        List<Assignment> assignments = studentService.getProgramAssignments(programId);
        return Response.ok("Задания программы").withData("assignments", assignments);
    }

    private Response handleSubmitAssignment(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.STUDENT);
        Map<String, Object> p = request.getPayload();
        long assignmentId = parseLong(p.get("assignmentId"));
        String fileName = asString(p.get("fileName"));
        String fileDataBase64 = asString(p.get("fileData"));
        String comment = asString(p.get("comment"));
        byte[] fileData = Base64.getDecoder().decode(fileDataBase64);
        studentService.submitAssignment(user.getId(), assignmentId, fileName, fileData, comment);
        return Response.ok("Ответ на задание загружен успешно");
    }

    private Response handleGetMySubmissions(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.STUDENT);
        List<Map<String, Object>> submissions = studentService.getMySubmissions(user.getId());
        return Response.ok("Мои ответы").withData("submissions", submissions);
    }

    // ---- Admin ----

    private Response handleCreateProgram(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        Map<String, Object> p = request.getPayload();
        EducationalProgram program = adminService.createProgram(
                asString(p.get("name")), asString(p.get("description")),
                parseInt(p.get("duration")), parseInt(p.get("maxStudents")));
        return Response.ok("Программа создана")
                .withData("programId", program.getId())
                .withData("programName", program.getName());
    }

    private Response handleCreateModule(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        Map<String, Object> p = request.getPayload();
        Module module = adminService.createModule(
                parseLong(p.get("programId")), asString(p.get("name")),
                asString(p.get("topic")), parseInt(p.get("credits")));
        return Response.ok("Модуль создан")
                .withData("moduleId", module.getId())
                .withData("moduleName", module.getName());
    }

    private Response handleRecordGrade(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        Map<String, Object> p = request.getPayload();
        adminService.recordStudentGrade(
                parseLong(p.get("studentId")), parseLong(p.get("moduleId")),
                parseDouble(p.get("score")));
        return Response.ok("Оценка выставлена");
    }

    private Response handleCreateAssignment(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        Map<String, Object> p = request.getPayload();
        Assignment assignment = adminService.createAssignment(
                parseLong(p.get("programId")), asString(p.get("title")),
                asString(p.get("description")), asString(p.get("dueDate")));
        return Response.ok("Задание создано")
                .withData("assignmentId", assignment.getId())
                .withData("title", assignment.getTitle());
    }

    private Response handleGetAssignmentSubmissions(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        Object raw = request.getPayload().get("assignmentId");
        Long assignmentId = (raw != null && !asString(raw).isEmpty()) ? parseLong(raw) : null;
        if (assignmentId != null && assignmentId <= 0) assignmentId = null;
        List<Map<String, Object>> submissions = adminService.getAssignmentSubmissions(assignmentId);
        return Response.ok("Ответы на задания").withData("submissions", submissions);
    }

    private Response handleAllAnalytics(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        List programEffectiveness = analyticsService.getProgramEffectiveness();
        List riskStudents = analyticsService.getRiskStudents(60.0);
        List topModules = analyticsService.getTopModules();
        return Response.ok("Полная аналитика")
                .withData("programEffectiveness", programEffectiveness)
                .withData("riskStudents", riskStudents)
                .withData("topModules", topModules);
    }

    private Response handleStudentList(Request request) {
        User user = authService.requireUser(request.getToken());
        requireRole(user, Role.ADMIN);
        List<Map<String, Object>> students = adminService.getStudentList();
        return Response.ok("Список студентов").withData("students", students);
    }

    // ---- Utilities ----

    private void requireRole(User user, Role expectedRole) {
        if (user.getRole() != expectedRole) {
            throw new IllegalArgumentException("Недостаточно прав. Требуется роль: " + expectedRole);
        }
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private int parseInt(Object value) {
        try { return Integer.parseInt(asString(value)); } catch (NumberFormatException e) { return 0; }
    }

    private long parseLong(Object value) {
        try { return Long.parseLong(asString(value)); } catch (NumberFormatException e) { return 0; }
    }

    private double parseDouble(Object value) {
        try { return Double.parseDouble(asString(value)); } catch (NumberFormatException e) { return 0.0; }
    }
}
