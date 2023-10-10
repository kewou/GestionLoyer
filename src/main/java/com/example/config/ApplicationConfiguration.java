package com.example.config;


import com.example.config.properties.InfoProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

@Configuration
public class ApplicationConfiguration {

    static {
        SpringDocUtils.getConfig().replaceWithClass(org.springframework.data.domain.Pageable.class, SpringDataWebProperties.Pageable.class);
    }

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

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200"); // Ajoutez votre URL front-end
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
