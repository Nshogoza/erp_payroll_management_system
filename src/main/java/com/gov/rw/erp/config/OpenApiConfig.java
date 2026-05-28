package com.gov.rw.erp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Government of Rwanda — ERP API",
        version = "1.0.0",
        description = "Enterprise Resource Planning Backend System for the Government of Rwanda. " +
                      "Use /api/auth/login to obtain a Bearer token, then click 'Authorize' above.",
        contact = @Contact(name = "Gov RW ERP Team", email = "erp@gov.rw")
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Paste your JWT token obtained from /api/auth/login"
)
public class OpenApiConfig {
}
