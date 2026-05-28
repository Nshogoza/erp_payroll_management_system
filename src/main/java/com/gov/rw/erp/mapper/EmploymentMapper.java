package com.gov.rw.erp.mapper;

import com.gov.rw.erp.dto.response.EmploymentResponse;
import com.gov.rw.erp.entity.Employment;
import org.springframework.stereotype.Component;

@Component
public class EmploymentMapper {

    public EmploymentResponse toResponse(Employment employment) {
        return EmploymentResponse.builder()
                .id(employment.getId())
                .code(employment.getCode())
                .employeeId(employment.getEmployee().getId())
                .employeeCode(employment.getEmployee().getCode())
                .employeeFullName(employment.getEmployee().getFirstName() + " " + employment.getEmployee().getLastName())
                .department(employment.getDepartment())
                .position(employment.getPosition())
                .baseSalary(employment.getBaseSalary())
                .status(employment.getStatus())
                .joiningDate(employment.getJoiningDate())
                .build();
    }
}
