package org.example.coursework2026marina.server.service;

import org.example.coursework2026marina.server.repository.AnalyticsRepository;

import java.util.List;
import java.util.Map;

public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<Map<String, Object>> getProgramEffectiveness() {
        return analyticsRepository.getProgramEffectiveness();
    }

    public List<Map<String, Object>> getRiskStudents(double threshold) {
        return analyticsRepository.getRiskStudents(threshold);
    }

    public List<Map<String, Object>> getTopModules() {
        return analyticsRepository.getTopModules();
    }

    public List<Map<String, Object>> getStudentProgress(String username) {
        return analyticsRepository.getStudentProgress(username);
    }
}
