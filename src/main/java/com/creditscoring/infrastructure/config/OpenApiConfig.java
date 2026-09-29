package com.creditscoring.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI creditScoringOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Motor de Scoring y Evaluacion de Creditos")
                        .description("API para registro, evaluacion de riesgo y amortizacion de solicitudes de credito. "
                                + "Documentacion interactiva disponible en /swagger-ui.html.")
                        .version("v1")
                        .contact(new Contact().name("Equipo de Riesgo Crediticio"))
                        .license(new License().name("Uso interno")));
    }
}
