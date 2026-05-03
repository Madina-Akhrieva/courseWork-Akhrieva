package org.example.coursework2026marina.server.model;

public class StudentGrade {
    private long id;
    private long studentId;
    private long moduleId;
    private double score;
    private String recordedAt;

    public StudentGrade() {
    }

    public StudentGrade(long id, long studentId, long moduleId, double score, String recordedAt) {
        this.id = id;
        this.studentId = studentId;
        this.moduleId = moduleId;
        this.score = score;
        this.recordedAt = recordedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }
}
