package com.gov.rw.erp.service;

import com.gov.rw.erp.dto.request.ApprovePayrollRequest;
import com.gov.rw.erp.dto.request.ProcessPayrollRequest;
import com.gov.rw.erp.dto.response.DeductionResponse;
import com.gov.rw.erp.entity.Deduction;
import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.entity.Employment;
import com.gov.rw.erp.enums.DeductionStatus;
import com.gov.rw.erp.enums.EmployeeStatus;
import com.gov.rw.erp.enums.EmploymentStatus;
import com.gov.rw.erp.event.PayrollApprovedEvent;
import com.gov.rw.erp.exception.PayrollAlreadyExistsException;
import com.gov.rw.erp.exception.ResourceNotFoundException;
import com.gov.rw.erp.mapper.DeductionMapper;
import com.gov.rw.erp.repository.DeductionRepository;
import com.gov.rw.erp.repository.EmploymentRepository;
import com.gov.rw.erp.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    // ── Payroll rates (Rwanda RSSB 2025) ─────────────────────────────────────
    private static final BigDecimal HOUSING_RATE    = new BigDecimal("0.14");
    private static final BigDecimal TRANSPORT_RATE  = new BigDecimal("0.14");
    private static final BigDecimal TAX_RATE        = new BigDecimal("0.30");
    private static final BigDecimal PENSION_RATE    = new BigDecimal("0.06");
    private static final BigDecimal MEDICAL_RATE    = new BigDecimal("0.05");
    private static final BigDecimal OTHER_RATE      = new BigDecimal("0.05");

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final DeductionRepository deductionRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionMapper deductionMapper;
    private final ApplicationEventPublisher eventPublisher;

    // ── Process Payroll ───────────────────────────────────────────────────────

    @Transactional
    public List<DeductionResponse> processPayroll(ProcessPayrollRequest request) {
        List<Employment> activeEmployments = employmentRepository
                .findByStatusAndEmployee_Status(EmploymentStatus.ACTIVE, EmployeeStatus.ACTIVE);

        if (activeEmployments.isEmpty()) {
            throw new ResourceNotFoundException("No active employees with active employment found.");
        }

        List<Deduction> created = new ArrayList<>();
        List<String> alreadyProcessed = new ArrayList<>();

        for (Employment employment : activeEmployments) {
            Employee employee = employment.getEmployee();
            if (deductionRepository.existsByEmployeeAndMonthAndYear(
                    employee, request.getMonth(), request.getYear())) {
                alreadyProcessed.add(employee.getCode());
                continue;
            }
            Deduction deduction = buildDeduction(employee, employment.getBaseSalary(),
                    request.getMonth(), request.getYear());
            created.add(deductionRepository.save(deduction));
        }

        if (created.isEmpty()) {
            throw new PayrollAlreadyExistsException(
                    "Payroll already processed for all active employees for "
                    + request.getMonth() + "/" + request.getYear()
                    + ". Skipped: " + alreadyProcessed);
        }

        return created.stream().map(deductionMapper::toResponse).toList();
    }

    // ── Approve Payroll ───────────────────────────────────────────────────────

    @Transactional
    public List<DeductionResponse> approvePayroll(ApprovePayrollRequest request) {
        List<Deduction> pending = deductionRepository.findByMonthAndYearAndStatus(
                request.getMonth(), request.getYear(), DeductionStatus.PENDING);

        if (pending.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No pending payroll found for " + request.getMonth() + "/" + request.getYear());
        }

        pending.forEach(d -> d.setStatus(DeductionStatus.PAID));
        List<Deduction> approved = deductionRepository.saveAll(pending);

        // Publish a data-snapshot event per employee — listener handles async email dispatch
        approved.forEach(d -> eventPublisher.publishEvent(new PayrollApprovedEvent(
                this,
                d.getId(),
                d.getEmployee().getEmail(),
                d.getEmployee().getFirstName(),
                d.getEmployee().getCode(),
                d.getNetSalary(),
                d.getMonth(),
                d.getYear()
        )));

        return approved.stream().map(deductionMapper::toResponse).toList();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<DeductionResponse> getPayrollByMonthAndYear(int month, int year) {
        return deductionRepository.findByMonthAndYear(month, year).stream()
                .map(deductionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeductionResponse> getMyPayslips() {
        Long userId = currentUserId();
        return deductionRepository.findByEmployee_Id(userId).stream()
                .map(deductionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeductionResponse getMyPayslipByMonthAndYear(int month, int year) {
        Long userId = currentUserId();
        return deductionRepository.findByEmployee_IdAndMonthAndYear(userId, month, year)
                .map(deductionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payslip not found for " + month + "/" + year));
    }

    // ── Salary calculation ────────────────────────────────────────────────────

    private Deduction buildDeduction(Employee employee, BigDecimal base, int month, int year) {
        BigDecimal housing   = scale(base.multiply(HOUSING_RATE));
        BigDecimal transport = scale(base.multiply(TRANSPORT_RATE));
        BigDecimal gross     = base.add(housing).add(transport);

        BigDecimal tax     = scale(base.multiply(TAX_RATE));
        BigDecimal pension = scale(base.multiply(PENSION_RATE));
        BigDecimal medical = scale(base.multiply(MEDICAL_RATE));
        BigDecimal other   = scale(base.multiply(OTHER_RATE));

        BigDecimal totalDeductions = tax.add(pension).add(medical).add(other);

        // Safety guard: deductions must not exceed gross salary
        if (totalDeductions.compareTo(gross) > 0) {
            totalDeductions = gross;
        }

        BigDecimal net = gross.subtract(totalDeductions);

        return Deduction.builder()
                .code(generateDeductionCode())
                .employee(employee)
                .baseSalary(base)
                .housingAllowance(housing)
                .transportAllowance(transport)
                .employeeTaxedAmount(tax)
                .pensionAmount(pension)
                .medicalInsuranceAmount(medical)
                .otherTaxedAmount(other)
                .grossSalary(gross)
                .netSalary(net)
                .month(month)
                .year(year)
                .status(DeductionStatus.PENDING)
                .build();
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateDeductionCode() {
        String code;
        do {
            code = "PAY-" + String.format("%08d", SECURE_RANDOM.nextInt(100_000_000));
        } while (deductionRepository.existsByCode(code));
        return code;
    }

    private Long currentUserId() {
        return ((CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getId();
    }
}
