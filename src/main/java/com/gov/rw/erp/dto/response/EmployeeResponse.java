package com.gov.rw.erp.dto.response;

import com.gov.rw.erp.enums.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
    private String mobile;
    private LocalDate dateOfBirth;
    private EmployeeStatus status;
}
