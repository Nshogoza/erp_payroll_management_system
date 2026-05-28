package com.gov.rw.erp.controller;

import com.gov.rw.erp.dto.request.CreateEmploymentRequest;
import com.gov.rw.erp.dto.response.ApiResponse;
import com.gov.rw.erp.dto.response.EmploymentResponse;
import com.gov.rw.erp.service.EmploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employment", description = "Employment records (department, position, salary)")
public class EmploymentController {

    private final EmploymentService employmentService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(
        summary = "Create employment record",
        description = "MANAGER / ADMIN only. Links an employee to a department, position, and base salary."
    )
    public ResponseEntity<ApiResponse<EmploymentResponse>> createEmployment(
            @Valid @RequestBody CreateEmploymentRequest request) {
        EmploymentResponse response = employmentService.createEmployment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Employment record created successfully.", 201));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "List all employment records", description = "MANAGER / ADMIN only.")
    public ResponseEntity<ApiResponse<List<EmploymentResponse>>> getAllEmployments() {
        return ResponseEntity.ok(
                ApiResponse.success(employmentService.getAllEmployments(), "Employment records retrieved."));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get employment record by ID", description = "MANAGER / ADMIN only.")
    public ResponseEntity<ApiResponse<EmploymentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(employmentService.getEmploymentById(id), "Employment record retrieved."));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get employment record by employee ID", description = "MANAGER / ADMIN only.")
    public ResponseEntity<ApiResponse<EmploymentResponse>> getByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(
                ApiResponse.success(employmentService.getEmploymentByEmployeeId(employeeId),
                        "Employment record retrieved."));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my employment record", description = "Returns the authenticated employee's employment details.")
    public ResponseEntity<ApiResponse<EmploymentResponse>> getMyEmployment() {
        return ResponseEntity.ok(
                ApiResponse.success(employmentService.getMyEmployment(), "Employment record retrieved."));
    }
}
