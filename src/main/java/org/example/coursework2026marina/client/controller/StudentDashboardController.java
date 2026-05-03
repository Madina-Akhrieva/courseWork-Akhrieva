package org.example.coursework2026marina.client.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.coursework2026marina.client.net.NetworkClient;
import org.example.coursework2026marina.common.Command;
import org.example.coursework2026marina.common.Request;
import org.example.coursework2026marina.common.Response;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentDashboardController {
    @FXML private Label userLabel;
    // Программы
    @FXML private ListView<String> programsListView;
    // Успеваемость
    @FXML private TableView<Map<String, Object>> progressTable;
    @FXML private TableColumn<Map<String, Object>, String> moduleColumn;
    @FXML private TableColumn<Map<String, Object>, String> scoreColumn;
    @FXML private TableColumn<Map<String, Object>, String> recordedAtColumn;
    // Задания
    @FXML private TableView<Map<String, Object>> assignmentsTable;
    @FXML private TableColumn<Map<String, Object>, String> assignmentTitleColumn;
    @FXML private TableColumn<Map<String, Object>, String> assignmentDescColumn;
    @FXML private TableColumn<Map<String, Object>, String> assignmentDueColumn;
    @FXML private Label selectedFileLabel;
    @FXML private TextArea submitCommentArea;
    // Мои ответы
    @FXML private TableView<Map<String, Object>> mySubmissionsTable;
    @FXML private TableColumn<Map<String, Object>, String> subTitleColumn;
    @FXML private TableColumn<Map<String, Object>, String> subFileColumn;
    @FXML private TableColumn<Map<String, Object>, String> subDateColumn;
    // YouTube курсы
    @FXML private ListView<String> youtubeCoursesListView;
    @FXML private Label youtubeCourseInfoLabel;

    private String token;
    private NetworkClient networkClient;
    private List<Map<String, Object>> availablePrograms = Collections.emptyList();
    private File selectedSubmitFile;

    // YouTube courses data
    private static final class YoutubeCourse {
        final String title;
        final String channel;
        final String url;
        YoutubeCourse(String title, String channel, String url) {
            this.title = title;
            this.channel = channel;
            this.url = url;
        }
        @Override public String toString() {
            return "▶  " + title + "  —  " + channel;
        }
    }

    private final Map<String, List<YoutubeCourse>> youtubeCatalog = new LinkedHashMap<>();
    private List<YoutubeCourse> currentYoutubeCourses = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            if (moduleColumn != null) {
                moduleColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>("Модуль #" + value(row.getValue().get("moduleId"))));
            }
            if (scoreColumn != null) {
                scoreColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("score"))));
            }
            if (recordedAtColumn != null) {
                recordedAtColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("recordedAt"))));
            }

            if (assignmentTitleColumn != null) {
                assignmentTitleColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("title"))));
            }
            if (assignmentDescColumn != null) {
                assignmentDescColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("description"))));
            }
            if (assignmentDueColumn != null) {
                assignmentDueColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("dueDate"))));
            }

            if (subTitleColumn != null) {
                subTitleColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("assignmentTitle"))));
            }
            if (subFileColumn != null) {
                subFileColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("fileName"))));
            }
            if (subDateColumn != null) {
                subDateColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(value(row.getValue().get("submittedAt"))));
            }
        } catch (Exception ignored) {
        }
        initYoutubeCatalog();
    }

    // ---- YouTube Catalogue ----
    private void initYoutubeCatalog() {
        youtubeCatalog.put("java", Arrays.asList(
            new YoutubeCourse("Java для начинающих — полный курс", "freeCodeCamp", "https://www.youtube.com/watch?v=A74TOX803D0"),
            new YoutubeCourse("Java Tutorial for Beginners", "Programming with Mosh", "https://www.youtube.com/watch?v=eIrMbAQSU34"),
            new YoutubeCourse("Spring Boot Full Course", "Amigoscode", "https://www.youtube.com/watch?v=9SGDpanrc8U"),
            new YoutubeCourse("Java Collections Framework", "Telusko", "https://www.youtube.com/watch?v=GdAon80-0KA"),
            new YoutubeCourse("Java Concurrency & Multithreading", "Jakob Jenkov", "https://www.youtube.com/watch?v=YdlnEWC-7Wo")
        ));
        youtubeCatalog.put("python", Arrays.asList(
            new YoutubeCourse("Python для начинающих — полный курс", "freeCodeCamp", "https://www.youtube.com/watch?v=rfscVS0vtbw"),
            new YoutubeCourse("Python Tutorial for Beginners", "Programming with Mosh", "https://www.youtube.com/watch?v=_uQrJ0TkZlc"),
            new YoutubeCourse("Python Full Course for Beginners", "Bro Code", "https://www.youtube.com/watch?v=XKHEtdqhLK8"),
            new YoutubeCourse("Django Web Framework Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=F5mRW0jo-U4"),
            new YoutubeCourse("Python Data Science Handbook", "Corey Schafer", "https://www.youtube.com/watch?v=vmEHCJofslg")
        ));
        youtubeCatalog.put("web", Arrays.asList(
            new YoutubeCourse("HTML & CSS Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=mU6anWqZJcc"),
            new YoutubeCourse("JavaScript Full Course for Beginners", "freeCodeCamp", "https://www.youtube.com/watch?v=PkZNo7MFNFg"),
            new YoutubeCourse("React Tutorial for Beginners", "Programming with Mosh", "https://www.youtube.com/watch?v=SqcY0GlETPk"),
            new YoutubeCourse("Node.js Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=Oe421EPjeBE"),
            new YoutubeCourse("TypeScript Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=30LWjhZzg50")
        ));
        youtubeCatalog.put("algo", Arrays.asList(
            new YoutubeCourse("Data Structures & Algorithms Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=8hly31xKli0"),
            new YoutubeCourse("Algorithms and Data Structures", "freeCodeCamp", "https://www.youtube.com/watch?v=RBSGKlAvoiM"),
            new YoutubeCourse("MIT 6.006 Introduction to Algorithms", "MIT OpenCourseWare", "https://www.youtube.com/watch?v=HtSuA80QTyo"),
            new YoutubeCourse("NeetCode — LeetCode Patterns", "NeetCode", "https://www.youtube.com/watch?v=9kadKeEpcY8"),
            new YoutubeCourse("CS50 — Harvard Introduction to CS", "Harvard", "https://www.youtube.com/watch?v=8mAITcNt710")
        ));
        youtubeCatalog.put("db", Arrays.asList(
            new YoutubeCourse("SQL Full Course for Beginners", "freeCodeCamp", "https://www.youtube.com/watch?v=HXV3zeQKqGY"),
            new YoutubeCourse("PostgreSQL Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=qw--VYLpxG4"),
            new YoutubeCourse("Database Design Course", "freeCodeCamp", "https://www.youtube.com/watch?v=ztHopE5Wnpc"),
            new YoutubeCourse("MongoDB Full Course", "freeCodeCamp", "https://www.youtube.com/watch?v=-56x56UppqQ"),
            new YoutubeCourse("Redis Crash Course", "Traversy Media", "https://www.youtube.com/watch?v=jgpVdJB2sKQ")
        ));
    }

    private void showYoutubeCourses(String category) {
        currentYoutubeCourses = youtubeCatalog.getOrDefault(category, Collections.emptyList());
        List<String> items = new ArrayList<>();
        for (YoutubeCourse c : currentYoutubeCourses) items.add(c.toString());
        if (youtubeCoursesListView != null) {
            youtubeCoursesListView.setItems(FXCollections.observableArrayList(items));
        }
        if (youtubeCourseInfoLabel != null) {
            youtubeCourseInfoLabel.setText("Найдено курсов: " + currentYoutubeCourses.size() + " — выберите и нажмите «Открыть в браузере»");
        }
    }

    @FXML private void handleShowJavaCourses(ActionEvent e) { showYoutubeCourses("java"); }
    @FXML private void handleShowPythonCourses(ActionEvent e) { showYoutubeCourses("python"); }
    @FXML private void handleShowWebCourses(ActionEvent e) { showYoutubeCourses("web"); }
    @FXML private void handleShowAlgoCourses(ActionEvent e) { showYoutubeCourses("algo"); }
    @FXML private void handleShowDbCourses(ActionEvent e) { showYoutubeCourses("db"); }

    @FXML
    private void handleOpenYouTubeCourse(ActionEvent event) {
        if (youtubeCoursesListView == null) return;
        int selected = youtubeCoursesListView.getSelectionModel().getSelectedIndex();
        if (selected < 0 || selected >= currentYoutubeCourses.size()) {
            showAlert(Alert.AlertType.WARNING, "Курс не выбран", "Выберите курс из списка");
            return;
        }
        String url = currentYoutubeCourses.get(selected).url;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть браузер: " + ex.getMessage());
        }
    }

    public void setUserInfo(String token, String fullName, NetworkClient networkClient) {
        this.token = token;
        this.networkClient = networkClient;
        userLabel.setText("Ученик: " + fullName);
        loadPrograms();
        loadProgress();
        loadMySubmissions();
    }

    // ---- Программы ----
    private void loadPrograms() {
        try {
            Response response = networkClient.sendRequest(Request.of(Command.GET_AVAILABLE_PROGRAMS).withToken(token));
            if (response.isSuccess()) {
                programsListView.getItems().clear();
                List<?> programs = (List<?>) response.getData().get("programs");
                availablePrograms = toMapRows(programs);
                availablePrograms.forEach(prog -> {
                    String text = value(prog.get("name")) + " (" + value(prog.get("duration")) + " дней)";
                    programsListView.getItems().add(text);
                });
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить программы: " + e.getMessage());
        }
    }

    @FXML
    private void handleEnrollProgram(ActionEvent event) {
        int selected = programsListView.getSelectionModel().getSelectedIndex();
        if (selected < 0 || selected >= availablePrograms.size()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите программу");
            return;
        }
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("programId", availablePrograms.get(selected).get("id"));
            Response response = networkClient.sendRequest(Request.of(Command.ENROLL_PROGRAM, payload).withToken(token));
            showAlert(Alert.AlertType.INFORMATION, "Результат", response.getMessage());
            loadProgress();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleLoadAssignments(ActionEvent event) {
        int selected = programsListView.getSelectionModel().getSelectedIndex();
        if (selected < 0 || selected >= availablePrograms.size()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите программу в списке слева");
            return;
        }
        try {
            Object programId = availablePrograms.get(selected).get("id");
            Map<String, Object> payload = new HashMap<>();
            payload.put("programId", programId);
            Response response = networkClient.sendRequest(Request.of(Command.GET_PROGRAM_ASSIGNMENTS, payload).withToken(token));
            if (response.isSuccess()) {
                List<Map<String, Object>> assignments = toMapRows((List<?>) response.getData().get("assignments"));
                assignmentsTable.setItems(FXCollections.observableArrayList(assignments));
                if (assignments.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "Задания", "У этой программы пока нет заданий");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        }
    }

    // ---- Загрузка файла-ответа ----
    @FXML
    private void handleChooseFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл с ответом");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Все файлы", "*.*"),
                new FileChooser.ExtensionFilter("Python", "*.py"),
                new FileChooser.ExtensionFilter("Документы", "*.pdf", "*.docx", "*.txt")
        );
        Stage stage = (Stage) userLabel.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            selectedSubmitFile = file;
            selectedFileLabel.setText(file.getName() + " (" + formatFileSize(file.length()) + ")");
        }
    }

    @FXML
    private void handleSubmitFile(ActionEvent event) {
        Map<String, Object> selectedAssignment = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите задание в таблице");
            return;
        }
        if (selectedSubmitFile == null || !selectedSubmitFile.exists()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите файл для загрузки");
            return;
        }
        if (selectedSubmitFile.length() > 10 * 1024 * 1024) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Файл слишком большой (максимум 10 МБ)");
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(selectedSubmitFile.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String comment = submitCommentArea != null ? submitCommentArea.getText().trim() : "";
            Map<String, Object> payload = new HashMap<>();
            payload.put("assignmentId", selectedAssignment.get("id"));
            payload.put("fileName", selectedSubmitFile.getName());
            payload.put("fileData", base64);
            payload.put("comment", comment);
            Response response = networkClient.sendRequest(Request.of(Command.SUBMIT_ASSIGNMENT, payload).withToken(token));
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    "Результат", response.getMessage());
            if (response.isSuccess()) {
                selectedSubmitFile = null;
                selectedFileLabel.setText("Файл не выбран");
                if (submitCommentArea != null) submitCommentArea.clear();
                loadMySubmissions();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось прочитать файл: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        }
    }

    // ---- Успеваемость ----
    private void loadProgress() {
        try {
            Response response = networkClient.sendRequest(Request.of(Command.GET_MY_PROGRESS).withToken(token));
            if (response.isSuccess()) {
                List<?> gradeList = extractGrades(response.getData());
                progressTable.setItems(FXCollections.observableArrayList(toMapRows(gradeList)));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка загрузки успеваемости: " + e.getMessage());
        }
    }

    // ---- Мои ответы ----
    private void loadMySubmissions() {
        try {
            Response response = networkClient.sendRequest(Request.of(Command.GET_MY_SUBMISSIONS).withToken(token));
            if (response.isSuccess()) {
                List<Map<String, Object>> submissions = toMapRows((List<?>) response.getData().get("submissions"));
                mySubmissionsTable.setItems(FXCollections.observableArrayList(submissions));
            }
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadPrograms();
        loadProgress();
        loadMySubmissions();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        networkClient.disconnect();
        ((Stage) userLabel.getScene().getWindow()).close();
    }

    // ---- Helpers ----
    @SuppressWarnings("unchecked")
    private List<?> extractGrades(Map<String, Object> data) {
        if (data == null) return null;
        Object direct = data.get("grades");
        if (direct instanceof List) return (List<?>) direct;
        Object nested = data.get("data");
        if (nested instanceof Map) {
            Object ng = ((Map<?, ?>) nested).get("grades");
            if (ng instanceof List) return (List<?>) ng;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toMapRows(List<?> list) {
        if (list == null) return Collections.emptyList();
        return list.stream()
                .filter(Map.class::isInstance)
                .map(row -> (Map<String, Object>) row)
                .collect(Collectors.toList());
    }

    private String value(Object v) { return v == null ? "—" : String.valueOf(v); }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message == null ? "—" : message);
        alert.showAndWait();
    }
}
