package com.gov.rw.erp.service;

import com.gov.rw.erp.dto.request.LoginRequest;
import com.gov.rw.erp.dto.request.OtpVerificationRequest;
import com.gov.rw.erp.dto.request.RegisterEmployeeRequest;
import com.gov.rw.erp.dto.response.AuthResponse;
import com.gov.rw.erp.dto.response.EmployeeResponse;
import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.enums.EmployeeStatus;
import com.gov.rw.erp.exception.DuplicateResourceException;
import com.gov.rw.erp.exception.InvalidOtpException;
import com.gov.rw.erp.exception.ResourceNotFoundException;
import com.gov.rw.erp.mapper.EmployeeMapper;
import com.gov.rw.erp.repository.EmployeeRepository;
import com.gov.rw.erp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmployeeMapper employeeMapper;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public EmployeeResponse register(RegisterEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account already exists for email: " + request.getEmail());
        }

        String otp = generateOtp();
        Employee employee = Employee.builder()
                .code(generateEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .mobile(request.getMobile())
                .dateOfBirth(request.getDateOfBirth())
                .status(EmployeeStatus.DISABLED)
                .otp(otp)
                .otpExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        Employee saved = employeeRepository.save(employee);
        emailService.sendOtpEmail(saved.getEmail(), saved.getFirstName(), otp);
        return employeeMapper.toResponse(saved);
    }

    @Transactional
    public void verifyOtp(OtpVerificationRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with email: " + request.getEmail()));

        if (employee.getStatus() == EmployeeStatus.ACTIVE) {
            throw new IllegalStateException("Account is already verified and active.");
        }

        if (employee.getOtp() == null || !employee.getOtp().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP provided.");
        }

        if (employee.getOtpExpiresAt() == null || employee.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired. Please request a new one.");
        }

        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setOtp(null);
        employee.setOtpExpiresAt(null);
        employeeRepository.save(employee);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(request.getEmail())
                .roles(roles)
                .build();
    }

    @Transactional
    public void resendOtp(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));

        if (employee.getStatus() == EmployeeStatus.ACTIVE) {
            throw new IllegalStateException("Account is already active. No OTP needed.");
        }

        String otp = generateOtp();
        employee.setOtp(otp);
        employee.setOtpExpiresAt(LocalDateTime.now().plusMinutes(15));
        employeeRepository.save(employee);
        emailService.sendOtpEmail(employee.getEmail(), employee.getFirstName(), otp);
    }

    private String generateOtp() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String generateEmployeeCode() {
        String code;
        do {
            code = "EMP-" + String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        } while (employeeRepository.existsByCode(code));
        return code;
    }
}
