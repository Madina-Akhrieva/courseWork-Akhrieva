package org.example.coursework2026marina.server.model;

import org.example.coursework2026marina.common.Role;

public class User {
    private long id;
    private String username;
    private String fullName;
    private String passwordHash;
    private Role role;

    public User() {
    }

    public User(long id, String username, String fullName, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
