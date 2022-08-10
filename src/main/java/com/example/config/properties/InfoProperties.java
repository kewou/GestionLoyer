package com.example.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "info")
@Getter
@Setter
public class InfoProperties {
    private String version;
    private String description;
    private String title;
    private String serverUrl;

}
