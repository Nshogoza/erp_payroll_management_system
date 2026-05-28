package com.gov.rw.erp.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * Carries a snapshot of all data needed for the payslip email so the async
 * listener never touches a detached JPA entity after the transaction closes.
 */
@Getter
public class PayrollApprovedEvent extends ApplicationEvent {

    private final Long deductionId;
    private final String employeeEmail;
    private final String employeeFirstName;
    private final String employeeCode;
    private final BigDecimal netSalary;
    private final int month;
    private final int year;

    public PayrollApprovedEvent(Object source,
                                Long deductionId,
                                String employeeEmail,
                                String employeeFirstName,
                                String employeeCode,
                                BigDecimal netSalary,
                                int month,
                                int year) {
        super(source);
        this.deductionId = deductionId;
        this.employeeEmail = employeeEmail;
        this.employeeFirstName = employeeFirstName;
        this.employeeCode = employeeCode;
        this.netSalary = netSalary;
        this.month = month;
        this.year = year;
    }
}
