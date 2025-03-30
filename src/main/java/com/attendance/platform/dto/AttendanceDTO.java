package com.attendance.platform.dto;

import com.attendance.platform.model.AttendanceStatus;
import com.attendance.platform.model.WorkLocation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalTime checkIn;

    private LocalTime checkOut;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    @NotNull(message = "Work location is required")
    private WorkLocation workLocation;

    private String notes;
}