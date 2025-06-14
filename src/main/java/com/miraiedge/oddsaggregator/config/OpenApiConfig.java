package com.miraiedge.oddsaggregator.config;

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
    public OpenAPI oddsAggregatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Odds Aggregator API")
                        .description("API for aggregating betting odds. Supports multiple sources")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MiraiEdge")
                                .email("support@miraiedge.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ));
    }
}