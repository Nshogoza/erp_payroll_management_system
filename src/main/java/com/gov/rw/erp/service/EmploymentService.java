package com.gov.rw.erp.service;

import com.gov.rw.erp.dto.request.CreateEmploymentRequest;
import com.gov.rw.erp.dto.response.EmploymentResponse;
import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.entity.Employment;
import com.gov.rw.erp.enums.EmploymentStatus;
import com.gov.rw.erp.exception.DuplicateResourceException;
import com.gov.rw.erp.exception.ResourceNotFoundException;
import com.gov.rw.erp.mapper.EmploymentMapper;
import com.gov.rw.erp.repository.EmploymentRepository;
import com.gov.rw.erp.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeService employeeService;
    private final EmploymentMapper employmentMapper;

    @Transactional
    public EmploymentResponse createEmployment(CreateEmploymentRequest request) {
        Employee employee = employeeService.getEmployeeEntityById(request.getEmployeeId());

        if (employmentRepository.existsByEmployee(employee)) {
            throw new DuplicateResourceException(
                    "An employment record already exists for employee: " + employee.getCode());
        }

        Employment employment = Employment.builder()
                .code(generateCode())
                .employee(employee)
                .department(request.getDepartment())
                .position(request.getPosition())
                .baseSalary(request.getBaseSalary())
                .status(EmploymentStatus.ACTIVE)
                .joiningDate(request.getJoiningDate())
                .build();

        return employmentMapper.toResponse(employmentRepository.save(employment));
    }

    @Transactional(readOnly = true)
    public List<EmploymentResponse> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(employmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmploymentResponse getEmploymentById(Long id) {
        return employmentRepository.findById(id)
                .map(employmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employment record not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public EmploymentResponse getEmploymentByEmployeeId(Long employeeId) {
        return employmentRepository.findByEmployee_Id(employeeId)
                .map(employmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employment record not found for employee id: " + employeeId));
    }

    @Transactional(readOnly = true)
    public EmploymentResponse getMyEmployment() {
        Long currentUserId = ((CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getId();
        return getEmploymentByEmployeeId(currentUserId);
    }

    private String generateCode() {
        String code;
        do {
            code = "EMPL-" + String.format("%06d", new Random().nextInt(1_000_000));
        } while (employmentRepository.existsByCode(code));
        return code;
    }
}
