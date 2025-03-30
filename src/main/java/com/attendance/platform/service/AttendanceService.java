package com.attendance.platform.service;

import com.attendance.platform.dto.AttendanceDTO;
import com.attendance.platform.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    List<AttendanceDTO> getAllAttendances();

    AttendanceDTO getAttendanceById(Long id);

    List<AttendanceDTO> getAttendancesByEmployeeId(Long employeeId);

    List<AttendanceDTO> getAttendancesByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

    List<AttendanceDTO> getAttendancesByDepartmentAndDateRange(String department, LocalDate startDate,
            LocalDate endDate);

    AttendanceDTO createAttendance(AttendanceDTO attendanceDTO);

    AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO);

    void deleteAttendance(Long id);

    // Analytics methods
    Long countAbsencesByEmployeeAndMonth(Long employeeId, int month, int year);

    Map<String, Long> getEmployeesWithMostAbsencesInMonth(int month, int year);

    Map<String, Long> getEmployeesWithMostRemoteDaysInMonth(int month, int year);

    // For AI insights
    List<AttendanceDTO> getDailyAttendance(LocalDate date);

    List<AttendanceDTO> getWeeklyAttendance(LocalDate startDate, LocalDate endDate);
}