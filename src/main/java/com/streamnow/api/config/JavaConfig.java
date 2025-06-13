package com.streamnow.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "streamnow")
@Getter
@Setter
public class JavaConfig {
    private String cdnBaseUrl;
}
