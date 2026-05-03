package org.example.coursework2026marina.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.coursework2026marina.client.net.NetworkClient;
import org.example.coursework2026marina.common.Command;
import org.example.coursework2026marina.common.Request;
import org.example.coursework2026marina.common.Response;
import org.example.coursework2026marina.common.Role;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField regUsernameField;
    @FXML
    private TextField regFullNameField;
    @FXML
    private PasswordField regPasswordField;
    @FXML
    private TabPane tabPane;

    private NetworkClient networkClient;

    @FXML
    public void initialize() {
        networkClient = new NetworkClient("localhost", 5051);
        if (!networkClient.connect()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось подключиться к серверу");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Введите логин и пароль");
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("password", password);
            Request request = Request.of(Command.LOGIN, payload);
            Response response = networkClient.sendRequest(request);

            if (response.isSuccess()) {
                String token = (String) response.getData().get("token");
                String role = (String) response.getData().get("role");
                String fullName = (String) response.getData().get("fullName");

                openDashboard(token, role, fullName);
                ((Stage) usernameField.getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка входа", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка сети", e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = regUsernameField.getText();
        String fullName = regFullNameField.getText();
        String password = regPasswordField.getText();

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Заполните все поля");
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("fullName", fullName);
            payload.put("password", password);
            payload.put("role", "STUDENT");
            Request request = Request.of(Command.REGISTER, payload);
            Response response = networkClient.sendRequest(request);

            if (response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Регистрация успешна. Войдите в аккаунт.");
                regUsernameField.clear();
                regFullNameField.clear();
                regPasswordField.clear();
                tabPane.getSelectionModel().selectFirst();
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка регистрации", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка сети", e.getMessage());
        }
    }

    private void openDashboard(String token, String role, String fullName) {
        try {
            String fxmlPath = "ADMIN".equals(role) 
                    ? "org/example/coursework2026marina/view/admin-dashboard.fxml" 
                    : "org/example/coursework2026marina/view/student-dashboard.fxml";
            
            java.net.URL fxmlUrl = getClass().getClassLoader().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IllegalStateException("Cannot find " + fxmlPath);
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            if ("ADMIN".equals(role)) {
                AdminDashboardController controller = loader.getController();
                controller.setUserInfo(token, fullName, networkClient);
            } else {
                StudentDashboardController controller = loader.getController();
                controller.setUserInfo(token, fullName, networkClient);
            }

            Stage stage = new Stage();
            stage.setTitle("Education System - " + fullName);
            stage.setScene(new Scene(root, "ADMIN".equals(role)?940:1040, 720));
            stage.show();
        } catch (Exception e) { Throwable r=e; while(r.getCause()!=null)r=r.getCause(); showAlert(Alert.AlertType.ERROR,"Ошибка загрузки",r.getClass().getSimpleName()+": "+r.getMessage()); }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
