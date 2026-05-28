package com.gov.rw.erp.mapper;

import com.gov.rw.erp.dto.response.DeductionResponse;
import com.gov.rw.erp.entity.Deduction;
import org.springframework.stereotype.Component;

@Component
public class DeductionMapper {

    public DeductionResponse toResponse(Deduction deduction) {
        return DeductionResponse.builder()
                .id(deduction.getId())
                .code(deduction.getCode())
                .employeeId(deduction.getEmployee().getId())
                .employeeCode(deduction.getEmployee().getCode())
                .employeeFullName(deduction.getEmployee().getFirstName() + " " + deduction.getEmployee().getLastName())
                .baseSalary(deduction.getBaseSalary())
                .housingAllowance(deduction.getHousingAllowance())
                .transportAllowance(deduction.getTransportAllowance())
                .employeeTaxedAmount(deduction.getEmployeeTaxedAmount())
                .pensionAmount(deduction.getPensionAmount())
                .medicalInsuranceAmount(deduction.getMedicalInsuranceAmount())
                .otherTaxedAmount(deduction.getOtherTaxedAmount())
                .grossSalary(deduction.getGrossSalary())
                .netSalary(deduction.getNetSalary())
                .month(deduction.getMonth())
                .year(deduction.getYear())
                .status(deduction.getStatus())
                .build();
    }
}
