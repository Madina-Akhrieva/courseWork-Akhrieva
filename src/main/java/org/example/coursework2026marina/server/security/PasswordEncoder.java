package org.example.coursework2026marina.server.security;

public interface PasswordEncoder {
    String encode(String raw);

    boolean matches(String raw, String encoded);
}
