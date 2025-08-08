package com.okoth.mortgage.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            version = "0.1.0",
            title = "\uD83D\uDEE0\uFE0F Mortgage API \uD83D\uDE80 ",
            termsOfService = "https://www.hfgroup.co.ke/documents/terms_and_conditions",
            description = "Allow institution to manage mortgage applications",
            contact = @Contact(email = "okoth.omondi@outlook.com", name = "Eng. Okoth Omondi"),
            license =
                @License(
                    name = "©2024/25 HFG Inc.",
                    url = "https://www.hfgroup.co.ke",
                    extensions = {})),
    servers = {
      @Server(url = "http://localhost:8080", description = "Local"),
      @Server(url = "https://mortgage.hfgroup.com", description = "Production")
    },
    security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class SpringFoxConfig {}
