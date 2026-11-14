package com.agroalerta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI agroAlertaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("AgroAlerta Cañete API")
                .description("""
                        API REST de control agrícola para Cañete: agricultores, parcelas,
                        cultivos, campañas, riegos, gastos, cosechas, ventas e indicadores
                        del dashboard.""")
                .version("v1")
                .contact(new Contact().name("AgroAlerta Cañete").email("soporte@agroalerta.pe"))
                .license(new License().name("Uso interno")));
    }
}
