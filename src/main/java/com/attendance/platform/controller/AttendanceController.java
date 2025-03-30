package com.attendance.platform.controller;

import com.attendance.platform.dto.AttendanceDTO;
import com.attendance.platform.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByEmployeeId(employeeId));
    }

    @GetMapping("/employee/{employeeId}/date-range")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByEmployeeIdAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                attendanceService.getAttendancesByEmployeeIdAndDateRange(employeeId, startDate, endDate));
    }

    @GetMapping("/department/{department}/date-range")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByDepartmentAndDateRange(
            @PathVariable String department,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                attendanceService.getAttendancesByDepartmentAndDateRange(department, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(@Valid @RequestBody AttendanceDTO attendanceDTO) {
        return new ResponseEntity<>(attendanceService.createAttendance(attendanceDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(
            @PathVariable Long id, @Valid @RequestBody AttendanceDTO attendanceDTO) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, attendanceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/absences/{employeeId}/{month}/{year}")
    public ResponseEntity<Long> countAbsencesByEmployeeAndMonth(
            @PathVariable Long employeeId,
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(attendanceService.countAbsencesByEmployeeAndMonth(employeeId, month, year));
    }

    @GetMapping("/analytics/absences/{month}/{year}")
    public ResponseEntity<Map<String, Long>> getEmployeesWithMostAbsencesInMonth(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(attendanceService.getEmployeesWithMostAbsencesInMonth(month, year));
    }

    @GetMapping("/analytics/remote-work/{month}/{year}")
    public ResponseEntity<Map<String, Long>> getEmployeesWithMostRemoteDaysInMonth(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(attendanceService.getEmployeesWithMostRemoteDaysInMonth(month, year));
    }
}