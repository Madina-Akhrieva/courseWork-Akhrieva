package org.example.coursework2026marina;

import javafx.application.Application;
import org.example.coursework2026marina.client.EducationClientApplication;

public class Launcher {
    public static void main(String[] args) {
        System.out.printf("Starting Education Client Application with args: %s%n", String.join(" ", args));
        Application.launch(EducationClientApplication.class, args);
        System.out.printf("Education Client Application has exited.%n");
    }

}
