package com.attendance.platform.service;

import java.time.LocalDate;
import java.util.Map;

public interface AiInsightService {
    String generateDailySummary(LocalDate date);

    String generateWeeklySummary(LocalDate startDate, LocalDate endDate);

    String getMostAbsentEmployeeForMonth(int month, int year);

    String getRemoteWorkSummaryForMonth(int month, int year);

    String answerAttendanceQuery(String query);
}