package com.attendance.platform.controller;

import com.attendance.platform.dto.EmployeeDTO;
import com.attendance.platform.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeViewController {

    private final EmployeeService employeeService;

    @GetMapping
    public String getAllEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees";
    }

    @GetMapping("/{id}")
    public String getEmployeeById(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        // In a real application, you would fetch attendance statistics here
        model.addAttribute("currentMonthAbsences", 0);
        model.addAttribute("remoteWorkDays", 0);
        model.addAttribute("attendanceRate", "95%");
        return "employee-detail";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "employee-form";
    }

    @PostMapping("/save")
    public String createEmployee(@ModelAttribute("employee") EmployeeDTO employeeDTO) {
        EmployeeDTO savedEmployee = employeeService.createEmployee(employeeDTO);
        return "redirect:/employees/" + savedEmployee.getId();
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employee-form";
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute("employee") EmployeeDTO employeeDTO) {
        employeeService.updateEmployee(id, employeeDTO);
        return "redirect:/employees/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
}