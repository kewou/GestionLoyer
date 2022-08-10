package com.example.config;


import com.example.config.properties.InfoProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    InfoProperties infoProperties;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().addServersItem(new Server().url(infoProperties.getServerUrl()))
                .info(new Info().title(infoProperties.getTitle()).description(infoProperties.getDescription())
                        .version(infoProperties.getVersion()));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModules(new ProblemModule(), new ConstraintViolationProblemModule());
    }
}
