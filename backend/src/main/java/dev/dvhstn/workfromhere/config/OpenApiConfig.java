package dev.dvhstn.workfromhere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Work From Here API")
                        .description("API for discovering remote work spaces (cafes and hot desks) in Belfast")
                        .version("v1"));
    }
}
