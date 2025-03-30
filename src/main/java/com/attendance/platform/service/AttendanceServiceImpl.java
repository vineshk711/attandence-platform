package com.attendance.platform.service;

import com.attendance.platform.dto.AttendanceDTO;
import com.attendance.platform.model.Attendance;
import com.attendance.platform.model.Employee;
import com.attendance.platform.repository.AttendanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;

    @Override
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
    }

    @Override
    public List<AttendanceDTO> getAttendancesByEmployeeId(Long employeeId) {
        Employee employee = employeeService.findEmployeeEntityById(employeeId);
        return attendanceRepository.findByEmployee(employee).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendancesByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate,
            LocalDate endDate) {
        Employee employee = employeeService.findEmployeeEntityById(employeeId);
        return attendanceRepository.findByEmployeeAndDateBetween(employee, startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendancesByDepartmentAndDateRange(String department, LocalDate startDate,
            LocalDate endDate) {
        return attendanceRepository.findByDepartmentAndDateBetween(department, startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO createAttendance(AttendanceDTO attendanceDTO) {
        Employee employee = employeeService.findEmployeeEntityById(attendanceDTO.getEmployeeId());

        // Check if an attendance record already exists for this employee on this date
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeAndDate(employee,
                attendanceDTO.getDate());

        if (existingAttendance.isPresent()) {
            // If record exists, update it instead of creating a new one
            Attendance attendance = existingAttendance.get();
            attendance.setCheckIn(attendanceDTO.getCheckIn());
            attendance.setCheckOut(attendanceDTO.getCheckOut());
            attendance.setStatus(attendanceDTO.getStatus());
            attendance.setWorkLocation(attendanceDTO.getWorkLocation());
            attendance.setNotes(attendanceDTO.getNotes());

            Attendance updatedAttendance = attendanceRepository.save(attendance);
            return mapToDTO(updatedAttendance);
        } else {
            // If no record exists, create a new one
            Attendance attendance = mapToEntity(attendanceDTO);
            Attendance savedAttendance = attendanceRepository.save(attendance);
            return mapToDTO(savedAttendance);
        }
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO) {
        Attendance existingAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));

        Employee employee = employeeService.findEmployeeEntityById(attendanceDTO.getEmployeeId());

        existingAttendance.setEmployee(employee);
        existingAttendance.setDate(attendanceDTO.getDate());
        existingAttendance.setCheckIn(attendanceDTO.getCheckIn());
        existingAttendance.setCheckOut(attendanceDTO.getCheckOut());
        existingAttendance.setStatus(attendanceDTO.getStatus());
        existingAttendance.setWorkLocation(attendanceDTO.getWorkLocation());
        existingAttendance.setNotes(attendanceDTO.getNotes());

        Attendance updatedAttendance = attendanceRepository.save(existingAttendance);
        return mapToDTO(updatedAttendance);
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
        attendanceRepository.delete(attendance);
    }

    @Override
    public Long countAbsencesByEmployeeAndMonth(Long employeeId, int month, int year) {
        Employee employee = employeeService.findEmployeeEntityById(employeeId);
        return attendanceRepository.countAbsencesByEmployeeAndMonth(employee, month, year);
    }

    @Override
    public Map<String, Long> getEmployeesWithMostAbsencesInMonth(int month, int year) {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> queryResult = attendanceRepository.findEmployeesWithMostAbsencesInMonth(month, year);

        for (Object[] row : queryResult) {
            Employee employee = (Employee) row[0];
            Long count = (Long) row[1];
            result.put(employee.getName(), count);
        }

        return result;
    }

    @Override
    public Map<String, Long> getEmployeesWithMostRemoteDaysInMonth(int month, int year) {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> queryResult = attendanceRepository.findEmployeesWithMostRemoteDaysInMonth(month, year);

        for (Object[] row : queryResult) {
            Employee employee = (Employee) row[0];
            Long count = (Long) row[1];
            result.put(employee.getName(), count);
        }

        return result;
    }

    @Override
    public List<AttendanceDTO> getDailyAttendance(LocalDate date) {
        return attendanceRepository.findAll().stream()
                .filter(a -> a.getDate().equals(date))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getWeeklyAttendance(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findAll().stream()
                .filter(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO mapToDTO(Attendance attendance) {
        return new AttendanceDTO(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getStatus(),
                attendance.getWorkLocation(),
                attendance.getNotes());
    }

    private Attendance mapToEntity(AttendanceDTO attendanceDTO) {
        Employee employee = employeeService.findEmployeeEntityById(attendanceDTO.getEmployeeId());

        return new Attendance(
                attendanceDTO.getId(),
                employee,
                attendanceDTO.getDate(),
                attendanceDTO.getCheckIn(),
                attendanceDTO.getCheckOut(),
                attendanceDTO.getStatus(),
                attendanceDTO.getWorkLocation(),
                attendanceDTO.getNotes());
    }
}