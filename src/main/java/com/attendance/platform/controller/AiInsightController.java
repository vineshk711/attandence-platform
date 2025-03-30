package com.attendance.platform.controller;

import com.attendance.platform.service.AiInsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class AiInsightController {

    private final AiInsightService aiInsightService;

    @GetMapping("/daily/{date}")
    public ResponseEntity<String> getDailySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(aiInsightService.generateDailySummary(date));
    }

    @GetMapping("/weekly")
    public ResponseEntity<String> getWeeklySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(aiInsightService.generateWeeklySummary(startDate, endDate));
    }

    @GetMapping("/absences/{month}/{year}")
    public ResponseEntity<String> getMostAbsentEmployeeForMonth(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(aiInsightService.getMostAbsentEmployeeForMonth(month, year));
    }

    @GetMapping("/remote-work/{month}/{year}")
    public ResponseEntity<String> getRemoteWorkSummaryForMonth(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(aiInsightService.getRemoteWorkSummaryForMonth(month, year));
    }

    @PostMapping("/query")
    public ResponseEntity<String> answerAttendanceQuery(@RequestBody String query) {
        return ResponseEntity.ok(aiInsightService.answerAttendanceQuery(query));
    }
}