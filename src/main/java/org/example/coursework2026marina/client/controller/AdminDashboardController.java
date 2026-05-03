package org.example.coursework2026marina.client.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.coursework2026marina.client.net.NetworkClient;
import org.example.coursework2026marina.common.Command;
import org.example.coursework2026marina.common.Request;
import org.example.coursework2026marina.common.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardController {
    // Программа
    @FXML private Label userLabel;
    @FXML private TextField programNameField;
    @FXML private TextField programDescField;
    @FXML private TextField durationField;
    @FXML private TextField maxStudentsField;
    // Модуль
    @FXML private TextField moduleNameField;
    @FXML private TextField moduleTopicField;
    @FXML private TextField moduleProgramIdField;
    // Оценка
    @FXML private TextField studentIdField;
    @FXML private TextField moduleIdField;
    @FXML private TextField scoreField;
    // Задание
    @FXML private TextField assignmentProgramIdField;
    @FXML private TextField assignmentTitleField;
    @FXML private TextField assignmentDescField;
    @FXML private TextField assignmentDueDateField;
    // Ответы студентов
    @FXML private TextField submissionsFilterField;
    @FXML private TableView<Map<String, Object>> submissionsTable;
    @FXML private TableColumn<Map<String, Object>, String> subAssignmentColumn;
    @FXML private TableColumn<Map<String, Object>, String> subStudentColumn;
    @FXML private TableColumn<Map<String, Object>, String> subFileColumn;
    @FXML private TableColumn<Map<String, Object>, String> subSizeColumn;
    @FXML private TableColumn<Map<String, Object>, String> subDateColumn;
    // Список студентов
    @FXML private TableView<Map<String, Object>> studentsTable;
    @FXML private TableColumn<Map<String, Object>, String> studentNameColumn;
    @FXML private TableColumn<Map<String, Object>, String> studentUsernameColumn;
    @FXML private TableColumn<Map<String, Object>, String> studentEnrolledColumn;
    // Аналитика
    @FXML private PieChart programDistributionChart;
    @FXML private BarChart<String, Number> programAvgScoreChart;
    @FXML private TableView<Map<String, Object>> programEffectivenessTable;
    @FXML private TableColumn<Map<String, Object>, String> programNameColumn;
    @FXML private TableColumn<Map<String, Object>, Number> programStudentsColumn;
    @FXML private TableColumn<Map<String, Object>, String> programAvgScoreColumn;
    @FXML private TableView<Map<String, Object>> riskStudentsTable;
    @FXML private TableColumn<Map<String, Object>, String> riskStudentNameColumn;
    @FXML private TableColumn<Map<String, Object>, String> riskStudentScoreColumn;
    @FXML private TableView<Map<String, Object>> topModulesTable;
    @FXML private TableColumn<Map<String, Object>, String> topModuleNameColumn;
    @FXML private TableColumn<Map<String, Object>, String> topModuleScoreColumn;

    private String token;
    private NetworkClient networkClient;

    @FXML
    public void initialize() {
        try {
            configureAnalyticsTables();
            configureSubmissionsTable();
            configureStudentsTable();
        } catch (Exception ignored) {
        }
    }

    private void configureSubmissionsTable() {
        if (subAssignmentColumn == null || subStudentColumn == null || subFileColumn == null || subSizeColumn == null || subDateColumn == null) return;
        subAssignmentColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("assignmentTitle"))));
        subStudentColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("studentName"))));
        subFileColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("fileName"))));
        subSizeColumn.setCellValueFactory(row -> {
            Object sz = row.getValue().get("fileSize");
            long bytes = sz instanceof Number ? ((Number) sz).longValue() : 0;
            return new ReadOnlyObjectWrapper<>(formatFileSize(bytes));
        });
        subDateColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("submittedAt"))));
    }

    private void configureStudentsTable() {
        if (studentNameColumn == null || studentUsernameColumn == null || studentEnrolledColumn == null) return;
        studentNameColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("fullName"))));
        studentUsernameColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("username"))));
        studentEnrolledColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("enrolledPrograms")) + " программ"));
    }

    private void configureAnalyticsTables() {
        if (programNameColumn == null || programStudentsColumn == null || programAvgScoreColumn == null
                || riskStudentNameColumn == null || riskStudentScoreColumn == null
                || topModuleNameColumn == null || topModuleScoreColumn == null) return;
        programNameColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("program_name"))));
        programStudentsColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asNumber(row.getValue().get("students_count"))));
        programAvgScoreColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(formatScore(row.getValue().get("avg_score"))));
        riskStudentNameColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("student_name"))));
        riskStudentScoreColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(formatScore(row.getValue().get("avg_score"))));
        topModuleNameColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(asString(row.getValue().get("module_name"))));
        topModuleScoreColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(formatScore(row.getValue().get("avg_score"))));
    }

    public void setUserInfo(String token, String fullName, NetworkClient networkClient) {
        this.token = token;
        this.networkClient = networkClient;
        userLabel.setText("Администратор: " + fullName);
        loadStudentList();
    }

    // ---- Программа ----
    @FXML
    private void handleCreateProgram(ActionEvent event) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", programNameField.getText());
            payload.put("description", programDescField.getText());
            payload.put("duration", Integer.parseInt(durationField.getText()));
            payload.put("maxStudents", Integer.parseInt(maxStudentsField.getText()));
            Response response = networkClient.sendRequest(Request.of(Command.CREATE_PROGRAM, payload).withToken(token));
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Результат", response.getMessage());
            if (response.isSuccess()) { programNameField.clear(); programDescField.clear(); durationField.clear(); maxStudentsField.clear(); }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage()); }
    }

    // ---- Модуль ----
    @FXML
    private void handleCreateModule(ActionEvent event) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("programId", Long.parseLong(moduleProgramIdField.getText()));
            payload.put("name", moduleNameField.getText());
            payload.put("topic", moduleTopicField.getText());
            payload.put("credits", 3);
            Response response = networkClient.sendRequest(Request.of(Command.CREATE_MODULE, payload).withToken(token));
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Результат", response.getMessage());
            if (response.isSuccess()) { moduleNameField.clear(); moduleTopicField.clear(); moduleProgramIdField.clear(); }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage()); }
    }

    // ---- Оценка ----
    @FXML
    private void handleRecordGrade(ActionEvent event) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("studentId", Long.parseLong(studentIdField.getText()));
            payload.put("moduleId", Long.parseLong(moduleIdField.getText()));
            payload.put("score", Double.parseDouble(scoreField.getText()));
            Response response = networkClient.sendRequest(Request.of(Command.RECORD_STUDENT_GRADE, payload).withToken(token));
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Результат", response.getMessage());
            if (response.isSuccess()) { studentIdField.clear(); moduleIdField.clear(); scoreField.clear(); }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage()); }
    }

    // ---- Задание ----
    @FXML
    private void handleCreateAssignment(ActionEvent event) {
        try {
            String dueDateText = assignmentDueDateField.getText().trim();
            if (dueDateText.isEmpty()) dueDateText = java.time.LocalDate.now().plusDays(7).toString();
            Map<String, Object> payload = new HashMap<>();
            payload.put("programId", Long.parseLong(assignmentProgramIdField.getText().trim()));
            payload.put("title", assignmentTitleField.getText().trim());
            payload.put("description", assignmentDescField.getText().trim());
            payload.put("dueDate", dueDateText);
            Response response = networkClient.sendRequest(Request.of(Command.CREATE_ASSIGNMENT, payload).withToken(token));
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, "Результат", response.getMessage());
            if (response.isSuccess()) { assignmentProgramIdField.clear(); assignmentTitleField.clear(); assignmentDescField.clear(); assignmentDueDateField.clear(); }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage()); }
    }

    @FXML
    private void handleViewSubmissions(ActionEvent event) {
        try {
            Map<String, Object> payload = new HashMap<>();
            String filterText = submissionsFilterField.getText().trim();
            if (!filterText.isEmpty()) {
                payload.put("assignmentId", Long.parseLong(filterText));
            }
            Response response = networkClient.sendRequest(Request.of(Command.GET_ASSIGNMENT_SUBMISSIONS, payload).withToken(token));
            if (response.isSuccess()) {
                List<Map<String, Object>> submissions = castToMapList(response.getData().get("submissions"));
                submissionsTable.setItems(FXCollections.observableArrayList(submissions));
                if (submissions.isEmpty()) showAlert(Alert.AlertType.INFORMATION, "Ответы", "Ответов не найдено");
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", response.getMessage());
            }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage()); }
    }

    // ---- Студенты ----
    @FXML
    private void handleViewStudents(ActionEvent event) {
        loadStudentList();
    }

    private void loadStudentList() {
        try {
            Response response = networkClient.sendRequest(Request.of(Command.GET_STUDENT_LIST).withToken(token));
            if (response.isSuccess()) {
                List<Map<String, Object>> students = castToMapList(response.getData().get("students"));
                studentsTable.setItems(FXCollections.observableArrayList(students));
            }
        } catch (Exception ignored) {}
    }

    // ---- Аналитика ----
    @FXML
    private void handleViewAnalytics(ActionEvent event) {
        try {
            Response response = networkClient.sendRequest(Request.of(Command.GET_ALL_ANALYTICS).withToken(token));
            if (response.isSuccess()) {
                Map<String, Object> data = extractAnalyticsData(response.getData());
                List<Map<String, Object>> programEffectiveness = castToMapList(data.get("programEffectiveness"));
                List<Map<String, Object>> riskStudents = castToMapList(data.get("riskStudents"));
                List<Map<String, Object>> topModules = castToMapList(data.get("topModules"));
                programEffectivenessTable.setItems(FXCollections.observableArrayList(programEffectiveness));
                riskStudentsTable.setItems(FXCollections.observableArrayList(riskStudents));
                topModulesTable.setItems(FXCollections.observableArrayList(topModules));
                updateProgramDistributionChart(programEffectiveness);
                updateProgramAvgScoreChart(programEffectiveness);
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", response.getMessage());
            }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage()); }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        networkClient.disconnect();
        ((Stage) userLabel.getScene().getWindow()).close();
    }

    // ---- Helpers ----
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message == null ? "—" : message);
        alert.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castToMapList(Object value) {
        if (!(value instanceof List)) return Collections.emptyList();
        return ((List<?>) value).stream()
                .filter(Map.class::isInstance)
                .map(item -> (Map<String, Object>) item)
                .collect(Collectors.toList());
    }

    private void updateProgramDistributionChart(List<Map<String, Object>> rows) {
        List<PieChart.Data> pieData = rows.stream()
                .map(row -> new PieChart.Data(asString(row.get("program_name")), asNumber(row.get("students_count")).doubleValue()))
                .collect(Collectors.toList());
        programDistributionChart.setData(FXCollections.observableArrayList(pieData));
    }

    private void updateProgramAvgScoreChart(List<Map<String, Object>> rows) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Средний балл");
        rows.forEach(row -> series.getData().add(new XYChart.Data<>(asString(row.get("program_name")), asNumber(row.get("avg_score")))));
        programAvgScoreChart.getData().setAll(series);
        programAvgScoreChart.setLegendVisible(false);
    }

    private String formatScore(Object value) {
        if (value == null) return "—";
        try { return String.format("%.2f", Double.parseDouble(String.valueOf(value))); }
        catch (NumberFormatException ignored) { return String.valueOf(value); }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    private String asString(Object value) {
        return value == null ? "—" : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractAnalyticsData(Map<String, Object> source) {
        if (source == null) return new HashMap<>();
        Object nested = source.get("data");
        if (nested instanceof Map) return (Map<String, Object>) nested;
        return source;
    }

    private Number asNumber(Object value) {
        if (value instanceof Number) return (Number) value;
        if (value == null) return 0;
        try { return Double.parseDouble(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return 0; }
    }
}

