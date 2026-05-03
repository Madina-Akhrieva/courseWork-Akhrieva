package org.example.coursework2026marina.server.model;

public class EducationalProgram {
    private long id;
    private String name;
    private String description;
    private int duration;
    private int maxStudents;

    public EducationalProgram() {
    }

    public EducationalProgram(long id, String name, String description, int duration, int maxStudents) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.maxStudents = maxStudents;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }
}
