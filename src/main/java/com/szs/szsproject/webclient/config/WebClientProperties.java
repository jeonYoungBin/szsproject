package com.szs.szsproject.webclient.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "endpoint")
public class WebClientProperties {
    private String key;
    private String uri;
} 