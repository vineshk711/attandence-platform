package com.attendance.platform.service;

import com.attendance.platform.dto.AttendanceDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiInsightServiceImpl implements AiInsightService {

    private final AttendanceService attendanceService;
    private final ChatLanguageModel chatLanguageModel;

    @Override
    public String generateDailySummary(LocalDate date) {
        List<AttendanceDTO> attendances = attendanceService.getDailyAttendance(date);

        if (attendances.isEmpty()) {
            return "No attendance records found for " + date;
        }

        String formattedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String prompt = buildDailySummaryPrompt(attendances, formattedDate);

        try {
            return chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            return "Error generating summary: " + e.getMessage();
        }
    }

    @Override
    public String generateWeeklySummary(LocalDate startDate, LocalDate endDate) {
        List<AttendanceDTO> attendances = attendanceService.getWeeklyAttendance(startDate, endDate);

        if (attendances.isEmpty()) {
            return "No attendance records found for the period from " + startDate + " to " + endDate;
        }

        String prompt = buildWeeklySummaryPrompt(attendances, startDate, endDate);

        try {
            return chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            return "Error generating summary: " + e.getMessage();
        }
    }

    @Override
    public String getMostAbsentEmployeeForMonth(int month, int year) {
        Map<String, Long> absencesByEmployee = attendanceService.getEmployeesWithMostAbsencesInMonth(month, year);

        if (absencesByEmployee.isEmpty()) {
            return "No absence records found for " + Month.of(month) + " " + year;
        }

        String prompt = "Given the following data about employee absences in " + Month.of(month) + " " + year +
                ", provide a brief analysis of who was absent the most and any patterns you notice:\n\n" +
                absencesByEmployee.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue() + " days")
                        .collect(Collectors.joining("\n"));

        try {
            return chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            return "Error generating analysis: " + e.getMessage();
        }
    }

    @Override
    public String getRemoteWorkSummaryForMonth(int month, int year) {
        Map<String, Long> remoteDaysByEmployee = attendanceService.getEmployeesWithMostRemoteDaysInMonth(month, year);

        if (remoteDaysByEmployee.isEmpty()) {
            return "No remote work records found for " + Month.of(month) + " " + year;
        }

        String prompt = "Given the following data about employees working remotely in " + Month.of(month) + " " + year +
                ", provide a brief analysis of remote work patterns and who worked remotely the most:\n\n" +
                remoteDaysByEmployee.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue() + " remote days")
                        .collect(Collectors.joining("\n"));

        try {
            return chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            return "Error generating analysis: " + e.getMessage();
        }
    }

    @Override
    public String answerAttendanceQuery(String query) {
        // Get recent attendance data to provide context
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        List<AttendanceDTO> recentAttendances = attendanceService.getWeeklyAttendance(oneMonthAgo, today);

        if (recentAttendances.isEmpty()) {
            return "No recent attendance data available to answer the query.";
        }

        String prompt = "Given the following attendance data for the past month, please answer this question: " +
                query + "\n\nAttendance data summary:\n" +
                recentAttendances.stream()
                        .limit(20) // Limit to avoid token limits
                        .map(a -> a.getDate() + ": " + a.getStatus() + " at " + a.getWorkLocation())
                        .collect(Collectors.joining("\n"));

        try {
            return chatLanguageModel.generate(prompt);
        } catch (Exception e) {
            return "Error answering query: " + e.getMessage();
        }
    }

    private String buildDailySummaryPrompt(List<AttendanceDTO> attendances, String formattedDate) {
        return "Create a concise but informative summary of today's attendance for " + formattedDate + ".\n" +
                "Include total number of employees present, absent, working remotely, and arrived late.\n" +
                "Data:\n" +
                attendances.stream()
                        .map(a -> "- Employee ID: " + a.getEmployeeId() +
                                ", Status: " + a.getStatus() +
                                ", Location: " + a.getWorkLocation() +
                                (a.getCheckIn() != null ? ", Check-in: " + a.getCheckIn() : "") +
                                (a.getCheckOut() != null ? ", Check-out: " + a.getCheckOut() : ""))
                        .collect(Collectors.joining("\n"));
    }

    private String buildWeeklySummaryPrompt(List<AttendanceDTO> attendances, LocalDate startDate, LocalDate endDate) {
        String dateRange = startDate.format(DateTimeFormatter.ofPattern("MMM d")) +
                " to " +
                endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));

        return "Create a concise weekly attendance summary for the period " + dateRange + ".\n" +
                "Include trends in attendance, remote work, and absences. Highlight any noteworthy patterns.\n" +
                "Data sample (first 20 records):\n" +
                attendances.stream()
                        .limit(20)
                        .map(a -> "- Date: " + a.getDate() +
                                ", Employee ID: " + a.getEmployeeId() +
                                ", Status: " + a.getStatus() +
                                ", Location: " + a.getWorkLocation())
                        .collect(Collectors.joining("\n"));
    }
}