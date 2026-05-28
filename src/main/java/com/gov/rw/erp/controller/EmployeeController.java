package com.gov.rw.erp.controller;

import com.gov.rw.erp.dto.response.ApiResponse;
import com.gov.rw.erp.dto.response.EmployeeResponse;
import com.gov.rw.erp.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employees", description = "Employee profile management")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "List all employees", description = "MANAGER / ADMIN only.")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getAllEmployees(), "Employees retrieved successfully."));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get employee by ID", description = "MANAGER / ADMIN only.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getEmployeeById(id), "Employee retrieved successfully."));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Returns the authenticated employee's own profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMyProfile() {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getMyProfile(), "Profile retrieved successfully."));
    }
}
