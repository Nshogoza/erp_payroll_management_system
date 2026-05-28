package com.gov.rw.erp.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProcessPayrollRequest {

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year is out of valid range")
    private Integer year;
}
