package org.example.coursework2026marina.server.security;

public final class PasswordEncoderFactory {
    private PasswordEncoderFactory() {
    }

    public static PasswordEncoder createDefault() {
        return new Sha256PasswordEncoder();
    }
}
