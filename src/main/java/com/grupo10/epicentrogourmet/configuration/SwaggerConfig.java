package com.grupo10.epicentrogourmet.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Epicentro Gourmet - API Hito 3")
                        .version("1.0")
                        .description("API REST - Equipo 10."));
    }
}