package com.gov.rw.erp.event;

import com.gov.rw.erp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayrollApprovedEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void onPayrollApproved(PayrollApprovedEvent event) {
        try {
            emailService.sendPayslipEmail(
                    event.getEmployeeEmail(),
                    event.getEmployeeFirstName(),
                    event.getEmployeeCode(),
                    event.getNetSalary(),
                    event.getMonth(),
                    event.getYear()
            );
        } catch (Exception e) {
            log.error("Failed to dispatch payslip email for deduction {}: {}",
                    event.getDeductionId(), e.getMessage());
        }
    }
}
