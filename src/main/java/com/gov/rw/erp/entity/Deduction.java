package com.gov.rw.erp.entity;

import com.gov.rw.erp.enums.DeductionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "deductions",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_deduction_employee_month_year",
        columnNames = {"employee_id", "month", "year"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    // EAGER: always needed for email dispatch after transaction closes
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal housingAllowance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal transportAllowance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal employeeTaxedAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pensionAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal medicalInsuranceAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal otherTaxedAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DeductionStatus status = DeductionStatus.PENDING;
}
