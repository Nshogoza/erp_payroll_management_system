package com.gov.rw.erp.controller;

import com.gov.rw.erp.dto.request.ApprovePayrollRequest;
import com.gov.rw.erp.dto.request.ProcessPayrollRequest;
import com.gov.rw.erp.dto.response.ApiResponse;
import com.gov.rw.erp.dto.response.DeductionResponse;
import com.gov.rw.erp.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payroll", description = "Payroll processing, approval and payslip retrieval")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(
        summary = "Process payroll",
        description = "MANAGER / ADMIN only. Generates PENDING payroll records for all active employees. " +
                      "Idempotent per employee per month/year — duplicates are silently skipped."
    )
    public ResponseEntity<ApiResponse<List<DeductionResponse>>> processPayroll(
            @Valid @RequestBody ProcessPayrollRequest request) {
        List<DeductionResponse> result = payrollService.processPayroll(request);
        return ResponseEntity.ok(ApiResponse.success(result,
                "Payroll processed for " + result.size() + " employee(s)."));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Approve payroll",
        description = "ADMIN only. Moves PENDING → PAID for the given month/year and " +
                      "asynchronously dispatches salary-credited emails to all affected employees."
    )
    public ResponseEntity<ApiResponse<List<DeductionResponse>>> approvePayroll(
            @Valid @RequestBody ApprovePayrollRequest request) {
        List<DeductionResponse> approved = payrollService.approvePayroll(request);
        return ResponseEntity.ok(ApiResponse.success(approved,
                "Payroll approved for " + approved.size() + " employee(s). Emails dispatched."));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(
        summary = "Get payroll by month / year",
        description = "MANAGER / ADMIN only. Returns all payroll records for the given period."
    )
    public ResponseEntity<ApiResponse<List<DeductionResponse>>> getPayroll(
            @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success(
                payrollService.getPayrollByMonthAndYear(month, year),
                "Payroll records retrieved."));
    }

    @GetMapping("/my-payslips")
    @Operation(
        summary = "Get all my payslips",
        description = "Returns all historical payslips for the authenticated employee."
    )
    public ResponseEntity<ApiResponse<List<DeductionResponse>>> getMyPayslips() {
        return ResponseEntity.ok(ApiResponse.success(
                payrollService.getMyPayslips(), "Payslips retrieved."));
    }

    @GetMapping("/my-payslips/{month}/{year}")
    @Operation(
        summary = "Get my payslip for a specific month / year",
        description = "Returns the payslip for the authenticated employee for the given period."
    )
    public ResponseEntity<ApiResponse<DeductionResponse>> getMyPayslip(
            @PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(ApiResponse.success(
                payrollService.getMyPayslipByMonthAndYear(month, year),
                "Payslip retrieved."));
    }
}
