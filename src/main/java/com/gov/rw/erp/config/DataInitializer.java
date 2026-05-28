package com.gov.rw.erp.config;

import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.enums.EmployeeStatus;
import com.gov.rw.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Seeds a default ADMIN+MANAGER user on first startup so the system is bootstrappable
 * without a pre-existing account. Change the credentials after first login.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (employeeRepository.existsByEmail("admin@gov.rw")) {
            return;
        }
        Employee admin = Employee.builder()
                .code("EMP-000001")
                .firstName("System")
                .lastName("Admin")
                .email("admin@gov.rw")
                .password(passwordEncoder.encode("Admin@12345"))
                .roles(Set.of("ROLE_ADMIN", "ROLE_MANAGER"))
                .status(EmployeeStatus.ACTIVE)
                .build();
        employeeRepository.save(admin);
        log.info("==> Default admin seeded  |  email: admin@gov.rw  |  password: Admin@12345  <== CHANGE THIS");
    }
}
