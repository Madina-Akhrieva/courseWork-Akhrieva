package org.example.coursework2026marina.server.model;

public class Assignment {
    private long id;
    private long programId;
    private String title;
    private String description;
    private String dueDate;

    public Assignment() {
    }

    public Assignment(long id, long programId, String title, String description, String dueDate) {
        this.id = id;
        this.programId = programId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
