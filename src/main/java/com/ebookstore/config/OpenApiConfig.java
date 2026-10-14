package com.ebookstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-Bookstore Platform API")
                .version("1.0.0")
                .description("RESTful Backend for the E-Bookstore Platform built with Spring Boot 3 and Java 21.\n" +
                             "Features: Catalogue browsing, facet filtering, cart management, 48-hour order cancellation, " +
                             "loyalty gift points redemption, 'Buy It Again' reordering, and smart recommendations.")
                .contact(new Contact()
                    .name("Capstone Project Team")
                    .email("support@ebookstore.example.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local Development Server")
            ));
    }
}
