package com.felipe.belo.mvp.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * Springdoc/OpenAPI configuration for the application.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Utility constructor to prevent instantiation.
     */
    public OpenApiConfig() { }

    /**
     * Builds the base OpenAPI specification used by Swagger UI.
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("MVP API")
                .description("API de exemplo refatorada para Spring Boot 3 / Java 21+.")
                .version("v1")
                .contact(new Contact()
                        .name("Felipe Belo")
                        .email("felipeefb@gmail.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        Server server = new Server()
                .url("http://localhost:8080")
                .description("Servidor local de desenvolvimento");

        SecurityScheme bearerAuth = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(info)
                .addServersItem(server)
                .schemaRequirement("BearerAuth", bearerAuth)
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }
}