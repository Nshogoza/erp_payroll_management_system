# ERP Payroll Management System

A Spring Boot REST API backend for employee and payroll management, built for the Government of Rwanda.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Email | Spring Mail (SMTP) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven |
| Utilities | Lombok |

---

## Features

- **JWT Authentication** with OTP verification (6-digit, 15-minute expiry, sent via email)
- **Role-based access control** — `ADMIN`, `MANAGER`, `EMPLOYEE`
- **Employee management** — register, update status, view profiles
- **Employment management** — assign positions, track employment history
- **Payroll processing** — compute gross, deductions, and net pay; approve and dispatch payslips by email
- **Async email dispatch** — payslips sent asynchronously after payroll approval
- **Swagger UI** for interactive API exploration

---

## Payroll Formulas

| Component | Calculation |
|---|---|
| Housing Allowance | 14% of base salary |
| Transport Allowance | 14% of base salary |
| **Gross Pay** | Base + Housing + Transport |
| Tax | 30% of base salary |
| Pension | 6% of base salary |
| Medical Insurance | 5% of base salary |
| Other Deductions | 5% of base salary |
| **Net Pay** | Gross − Total Deductions |

---

## Project Structure

```
src/main/java/com/gov/rw/erp/
├── config/         # Security, OpenAPI, DataInitializer
├── controller/     # AuthController, EmployeeController, EmploymentController, PayrollController
├── dto/
│   ├── request/    # Login, Register, OTP, Employment, Payroll DTOs
│   └── response/   # ApiResponse<T>, Auth, Employee, Employment, Deduction, Error DTOs
├── entity/         # Employee, Employment, Deduction
├── enums/          # EmployeeStatus, EmploymentStatus, DeductionStatus, Role
├── event/          # PayrollApprovedEvent + async listener
├── exception/      # GlobalExceptionHandler + custom exceptions
├── mapper/         # EmployeeMapper, EmploymentMapper, DeductionMapper
├── repository/     # Spring Data JPA repositories
├── security/       # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetails
└── service/        # AuthService, EmployeeService, EmploymentService, PayrollService, EmailService
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 14+

### 1. Clone the repository

```bash
git clone https://github.com/Nshogoza/erp_payroll_management_system.git
cd erp_payroll_management_system
```

### 2. Create the database

```sql
CREATE DATABASE erp_db;
```

### 3. Create your local `application.properties`

`application.properties` is git-ignored so that credentials never enter version
control. Copy the template:

```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

The template reads every secret from an environment variable, so no credential
is ever written to a file in the repository:

| Variable | Purpose |
|---|---|
| `DB_USERNAME` | PostgreSQL user |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | Base64 signing key for JWTs — generate with `openssl rand -base64 48` |
| `MAIL_USERNAME` | SMTP account used to send OTPs and payslips |
| `MAIL_PASSWORD` | SMTP password (for Gmail, an App Password) |

### 4. Export the environment variables

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=...
export JWT_SECRET="$(openssl rand -base64 48)"
export MAIL_USERNAME=...
export MAIL_PASSWORD=...
```

On Windows PowerShell:

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "..."
$env:JWT_SECRET  = "..."
$env:MAIL_USERNAME = "..."
$env:MAIL_PASSWORD = "..."
```

### 5. Run the application

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`.

---

## Default Admin Account

Seeded automatically on first startup by `DataInitializer`:

| Field | Value |
|---|---|
| Email | `admin@gov.rw` |
| Password | `Admin@12345` |
| Role | `ADMIN` |

> **Development credentials only.** This password is hardcoded in
> `DataInitializer` and is public in this README — change it immediately after
> first login, and do not deploy any environment that still accepts it.

---

## API Endpoints

### Auth — `/api/auth`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/login` | Public | Login with email + password, returns JWT + sends OTP |
| POST | `/verify-otp` | Public | Verify OTP to activate session |
| POST | `/resend-otp` | Public | Resend OTP to email |

### Employees — `/api/employees`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/` | ADMIN | Register a new employee |
| GET | `/` | ADMIN, MANAGER | List all employees |
| GET | `/{id}` | ADMIN, MANAGER | Get employee by ID |
| PATCH | `/{id}/status` | ADMIN | Update employee status |

### Employment — `/api/employments`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/` | ADMIN | Create employment record |
| GET | `/` | ADMIN, MANAGER | List all employment records |
| GET | `/{id}` | ADMIN, MANAGER | Get employment by ID |
| GET | `/employee/{employeeId}` | ADMIN, MANAGER | Get employment history for employee |

### Payroll — `/api/payroll`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/process` | MANAGER | Process payroll for a period |
| POST | `/approve` | ADMIN | Approve payroll and trigger payslip emails |
| GET | `/` | ADMIN, MANAGER | List all payroll records |
| GET | `/{employeeId}` | ADMIN, MANAGER | Get payroll records for employee |

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## Health Check

```
http://localhost:8080/actuator/health
```

---

## License

This project is developed for the Government of Rwanda. All rights reserved.
