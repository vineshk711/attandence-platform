package com.attendance.platform.controller;

import com.attendance.platform.dto.AttendanceDTO;
import com.attendance.platform.service.AiInsightService;
import com.attendance.platform.service.AttendanceService;
import com.attendance.platform.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final AiInsightService aiInsightService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("employeeCount", employeeService.getAllEmployees().size());
        model.addAttribute("today", LocalDate.now());

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);

        YearMonth currentMonth = YearMonth.now();
        model.addAttribute("currentMonth", currentMonth.getMonth().toString());
        model.addAttribute("currentYear", currentMonth.getYear());

        return "index";
    }

    @GetMapping("/insights/daily")
    public String dailyInsights(@RequestParam(required = false) LocalDate date, Model model) {
        if (date == null) {
            date = LocalDate.now();
        }

        String summary = aiInsightService.generateDailySummary(date);
        model.addAttribute("date", date);
        model.addAttribute("summary", summary);

        return "daily-insights";
    }

    @GetMapping("/insights/weekly")
    public String weeklyInsights(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {

        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
            endDate = startDate.plusDays(6);
        }

        String summary = aiInsightService.generateWeeklySummary(startDate, endDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("summary", summary);

        return "weekly-insights";
    }

    @GetMapping("/insights/absences")
    public String absenceInsights(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        if (month == null || year == null) {
            YearMonth currentMonth = YearMonth.now();
            month = currentMonth.getMonthValue();
            year = currentMonth.getYear();
        }

        String summary = aiInsightService.getMostAbsentEmployeeForMonth(month, year);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("summary", summary);

        return "absence-insights";
    }

    @GetMapping("/insights/remote-work")
    public String remoteWorkInsights(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        if (month == null || year == null) {
            YearMonth currentMonth = YearMonth.now();
            month = currentMonth.getMonthValue();
            year = currentMonth.getYear();
        }

        String summary = aiInsightService.getRemoteWorkSummaryForMonth(month, year);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("summary", summary);

        return "remote-work-insights";
    }

    @GetMapping("/ai-assistant")
    public String aiAssistant(Model model) {
        return "ai-assistant";
    }

    @PostMapping("/ai-assistant/query")
    public String processAiQuery(@RequestParam String query, Model model) {
        String answer = aiInsightService.answerAttendanceQuery(query);
        model.addAttribute("query", query);
        model.addAttribute("answer", answer);
        return "ai-assistant";
    }
}