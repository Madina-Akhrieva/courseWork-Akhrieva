package org.example.coursework2026marina.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

//main
public class EducationClientApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlResource = getClass().getClassLoader().getResource("org/example/coursework2026marina/view/login-view.fxml");
        if (fxmlResource == null) {
            throw new IllegalStateException("Cannot find login-view.fxml in resources");
        }
        FXMLLoader loader = new FXMLLoader(fxmlResource);
        Scene scene = new Scene(loader.load(), 400, 300);
        primaryStage.setTitle("Education System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
