package com.gov.rw.erp.controller;

import com.gov.rw.erp.dto.request.LoginRequest;
import com.gov.rw.erp.dto.request.OtpVerificationRequest;
import com.gov.rw.erp.dto.request.RegisterEmployeeRequest;
import com.gov.rw.erp.dto.response.ApiResponse;
import com.gov.rw.erp.dto.response.AuthResponse;
import com.gov.rw.erp.dto.response.EmployeeResponse;
import com.gov.rw.erp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, registration and OTP verification")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Register a new employee",
        description = "MANAGER or ADMIN only. Creates the account in DISABLED state and sends a 6-digit OTP to the employee's email."
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> register(
            @Valid @RequestBody RegisterEmployeeRequest request) {
        EmployeeResponse employee = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employee,
                        "Employee registered. OTP sent to " + employee.getEmail() + ".", 201));
    }

    @PostMapping("/verify-otp")
    @Operation(
        summary = "Verify email OTP",
        description = "Validates the 6-digit OTP sent to the employee's email and activates the account."
    )
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Account verified and activated successfully."));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticate with email + password. Returns a Bearer JWT to be used in the Authorization header."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful."));
    }

    @PostMapping("/resend-otp")
    @Operation(
        summary = "Resend OTP",
        description = "Resends a fresh OTP (valid 15 min) to the employee's registered email."
    )
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return ResponseEntity.ok(ApiResponse.success(null, "OTP resent to " + email + "."));
    }
}
