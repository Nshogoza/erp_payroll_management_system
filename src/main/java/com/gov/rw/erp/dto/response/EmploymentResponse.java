package com.gov.rw.erp.dto.response;

import com.gov.rw.erp.enums.EmploymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentResponse {

    private Long id;
    private String code;
    private Long employeeId;
    private String employeeCode;
    private String employeeFullName;
    private String department;
    private String position;
    private BigDecimal baseSalary;
    private EmploymentStatus status;
    private LocalDate joiningDate;
}
