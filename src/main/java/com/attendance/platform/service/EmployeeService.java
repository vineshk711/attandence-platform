package com.attendance.platform.service;

import com.attendance.platform.dto.EmployeeDTO;
import com.attendance.platform.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(Long id);

    EmployeeDTO getEmployeeByEmail(String email);

    EmployeeDTO getEmployeeByEmployeeId(String employeeId);

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);

    void deleteEmployee(Long id);

    // Internal method for other services to use
    Employee findEmployeeEntityById(Long id);
}