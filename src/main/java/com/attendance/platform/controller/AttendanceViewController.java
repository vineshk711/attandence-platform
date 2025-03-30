package com.attendance.platform.controller;

import com.attendance.platform.dto.AttendanceDTO;
import com.attendance.platform.model.AttendanceStatus;
import com.attendance.platform.model.WorkLocation;
import com.attendance.platform.service.AttendanceService;
import com.attendance.platform.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceViewController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String getAllAttendances(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<AttendanceDTO> attendances = attendanceService.getDailyAttendance(date);

        // Calculate counts for different attendance types
        long presentCount = attendances.stream().filter(a -> a.getStatus().name().equals("PRESENT")).count();
        long absentCount = attendances.stream().filter(a -> a.getStatus().name().equals("ABSENT")).count();
        long remoteCount = attendances.stream().filter(a -> a.getWorkLocation().name().equals("REMOTE")).count();

        model.addAttribute("attendances", attendances);
        model.addAttribute("date", date);
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("remoteCount", remoteCount);

        return "attendances";
    }

    @GetMapping("/employee/{employeeId}")
    public String getAttendancesByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        if (startDate == null || endDate == null) {
            // Default to current month if no dates provided
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        List<AttendanceDTO> attendances = attendanceService.getAttendancesByEmployeeIdAndDateRange(employeeId,
                startDate, endDate);

        // Calculate counts for different attendance types
        long presentCount = attendances.stream().filter(a -> a.getStatus().name().equals("PRESENT")).count();
        long absentCount = attendances.stream().filter(a -> a.getStatus().name().equals("ABSENT")).count();
        long remoteCount = attendances.stream().filter(a -> a.getWorkLocation().name().equals("REMOTE")).count();

        model.addAttribute("employee", employeeService.getEmployeeById(employeeId));
        model.addAttribute("attendances", attendances);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("remoteCount", remoteCount);

        return "employee-attendance";
    }

    @GetMapping("/department/{department}")
    public String getAttendancesByDepartment(
            @PathVariable String department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        if (startDate == null || endDate == null) {
            // Default to current month if no dates provided
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        List<AttendanceDTO> attendances = attendanceService.getAttendancesByDepartmentAndDateRange(department,
                startDate, endDate);

        // Calculate counts for different attendance types
        long presentCount = attendances.stream().filter(a -> a.getStatus().name().equals("PRESENT")).count();
        long absentCount = attendances.stream().filter(a -> a.getStatus().name().equals("ABSENT")).count();
        long remoteCount = attendances.stream().filter(a -> a.getWorkLocation().name().equals("REMOTE")).count();

        model.addAttribute("department", department);
        model.addAttribute("attendances", attendances);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("remoteCount", remoteCount);

        return "department-attendance";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("attendance", new AttendanceDTO());
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("statuses", AttendanceStatus.values());
        model.addAttribute("workLocations", WorkLocation.values());

        return "attendance-form";
    }

    @PostMapping("/save")
    public String saveAttendance(@ModelAttribute("attendance") AttendanceDTO attendanceDTO,
            RedirectAttributes redirectAttributes) {
        try {
            if (attendanceDTO.getId() == null) {
                attendanceService.createAttendance(attendanceDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Attendance record created successfully");
            } else {
                attendanceService.updateAttendance(attendanceDTO.getId(), attendanceDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Attendance record updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/attendance";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("statuses", AttendanceStatus.values());
        model.addAttribute("workLocations", WorkLocation.values());

        return "attendance-form";
    }

    @GetMapping("/{id}/delete")
    public String deleteAttendance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.deleteAttendance(id);
            redirectAttributes.addFlashAttribute("successMessage", "Attendance record deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/attendance";
    }

    @GetMapping("/analytics")
    public String showAnalytics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        if (month == null || year == null) {
            YearMonth currentMonth = YearMonth.now();
            month = currentMonth.getMonthValue();
            year = currentMonth.getYear();
        }

        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("absencesByEmployee",
                attendanceService.getEmployeesWithMostAbsencesInMonth(month, year));
        model.addAttribute("remoteWorkByEmployee",
                attendanceService.getEmployeesWithMostRemoteDaysInMonth(month, year));

        return "attendance-analytics";
    }
}