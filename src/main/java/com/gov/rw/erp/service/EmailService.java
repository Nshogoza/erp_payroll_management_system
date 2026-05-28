package com.gov.rw.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.institution.name:Government of Rwanda}")
    private String institutionName;

    @Async
    public void sendOtpEmail(String toEmail, String firstName, String otp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject(institutionName + " ERP — Email Verification");
            msg.setText(String.format(
                    "Dear %s,%n%n" +
                    "Your One-Time Password (OTP) for email verification is:%n%n" +
                    "  %s%n%n" +
                    "This OTP expires in 15 minutes. Do not share it with anyone.%n%n" +
                    "Regards,%n%s ERP System",
                    firstName, otp, institutionName));
            mailSender.send(msg);
            log.info("OTP email dispatched to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendPayslipEmail(String toEmail,
                                  String firstName,
                                  String employeeCode,
                                  BigDecimal netSalary,
                                  int month,
                                  int year) {
        try {
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject(String.format("Salary Credited — %s %d", monthName, year));
            msg.setText(String.format(
                    "Dear %s,%n%n" +
                    "Your salary of %s/%d from %s amounting to %.2f RWF has been credited " +
                    "to your %s account successfully.%n%n" +
                    "Regards,%n%s ERP System",
                    firstName, monthName, year, institutionName,
                    netSalary, employeeCode, institutionName));
            mailSender.send(msg);
            log.info("Payslip email dispatched to {} for {}/{}", toEmail, monthName, year);
        } catch (Exception e) {
            log.error("Failed to send payslip email to {}: {}", toEmail, e.getMessage());
        }
    }
}
