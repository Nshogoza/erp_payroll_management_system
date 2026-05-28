---
name: project-erp-impl
description: Full ERP backend implementation — packages, patterns, and key design decisions
metadata:
  type: project
---

Full Spring Boot 4.x ERP backend implemented for Government of Rwanda (com.gov.rw.erp).

**Why:** Government ERP system for employee management, payroll processing and payslip delivery.

**How to apply:** When asked to extend the system, follow the existing layered pattern (Entity → Repository → Service → Controller → DTO/Mapper).

## Package layout
- `entity/` — Employee, Employment, Deduction (JPA + Lombok)
- `enums/` — EmployeeStatus, EmploymentStatus, DeductionStatus, Role
- `repository/` — Spring Data JPA repositories
- `security/` — CustomUserDetails, CustomUserDetailsService, JwtTokenProvider, JwtAuthenticationFilter
- `config/` — SecurityConfig, OpenApiConfig, DataInitializer
- `dto/request/` — LoginRequest, RegisterEmployeeRequest, OtpVerificationRequest, CreateEmploymentRequest, ProcessPayrollRequest, ApprovePayrollRequest
- `dto/response/` — ApiResponse<T>, ErrorResponse, AuthResponse, EmployeeResponse, EmploymentResponse, DeductionResponse
- `mapper/` — EmployeeMapper, EmploymentMapper, DeductionMapper
- `service/` — AuthService, EmployeeService, EmploymentService, PayrollService, EmailService
- `controller/` — AuthController, EmployeeController, EmploymentController, PayrollController
- `event/` — PayrollApprovedEvent (data snapshot, not JPA entity), PayrollApprovedEventListener (@Async)
- `exception/` — GlobalExceptionHandler (@RestControllerAdvice), ResourceNotFoundException, DuplicateResourceException, PayrollAlreadyExistsException, InvalidOtpException

## Key design decisions
- OTP uses SecureRandom, stored on Employee (otp + otpExpiresAt), cleared on verify
- PayrollApprovedEvent carries a plain-data snapshot (not the JPA entity) to avoid LazyInit in async listener
- Deduction.employee is EAGER (always needed for email dispatch)
- DataInitializer seeds admin@gov.rw / Admin@12345 on first boot
- Roles stored as "ROLE_MANAGER", "ROLE_ADMIN", "ROLE_EMPLOYEE" strings; @PreAuthorize uses hasRole('MANAGER') etc.
- Payroll formulas: Housing=14%, Transport=14%, Tax=30%, Pension=6%, Medical=5%, Other=5% — all of base salary
- @EnableAsync on ErpApplication; emails dispatched via @Async EmailService methods
- pom.xml: jjwt 0.12.6, springdoc-openapi-starter-webmvc-ui 2.8.8 (may need version bump for Spring Boot 4.x)
- DB: PostgreSQL, ddl-auto=update, no Flyway yet
