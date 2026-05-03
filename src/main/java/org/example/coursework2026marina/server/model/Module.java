package org.example.coursework2026marina.server.model;

public class Module {
    private long id;
    private long programId;
    private String name;
    private String topic;
    private int credits;

    public Module() {
    }

    public Module(long id, long programId, String name, String topic, int credits) {
        this.id = id;
        this.programId = programId;
        this.name = name;
        this.topic = topic;
        this.credits = credits;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
