package com.gov.rw.erp.dto.response;

import com.gov.rw.erp.enums.DeductionStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductionResponse {

    private Long id;
    private String code;
    private Long employeeId;
    private String employeeCode;
    private String employeeFullName;
    private BigDecimal baseSalary;
    private BigDecimal housingAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal employeeTaxedAmount;
    private BigDecimal pensionAmount;
    private BigDecimal medicalInsuranceAmount;
    private BigDecimal otherTaxedAmount;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private Integer month;
    private Integer year;
    private DeductionStatus status;
}
