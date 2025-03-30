package com.attendance.platform.repository;

import com.attendance.platform.model.Attendance;
import com.attendance.platform.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);

    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.employee.department = ?1 AND a.date BETWEEN ?2 AND ?3")
    List<Attendance> findByDepartmentAndDateBetween(String department, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = ?1 AND a.status = 'ABSENT' AND FUNCTION('MONTH', a.date) = ?2 AND FUNCTION('YEAR', a.date) = ?3")
    Long countAbsencesByEmployeeAndMonth(Employee employee, int month, int year);

    @Query("SELECT a.employee, COUNT(a) FROM Attendance a WHERE a.status = 'ABSENT' AND FUNCTION('MONTH', a.date) = ?1 AND FUNCTION('YEAR', a.date) = ?2 GROUP BY a.employee ORDER BY COUNT(a) DESC")
    List<Object[]> findEmployeesWithMostAbsencesInMonth(int month, int year);

    @Query("SELECT a.employee, COUNT(a) FROM Attendance a WHERE a.workLocation = 'REMOTE' AND FUNCTION('MONTH', a.date) = ?1 AND FUNCTION('YEAR', a.date) = ?2 GROUP BY a.employee ORDER BY COUNT(a) DESC")
    List<Object[]> findEmployeesWithMostRemoteDaysInMonth(int month, int year);
}