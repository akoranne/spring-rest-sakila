package com.example.app.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@ConfigurationPropertiesScan(basePackages = "com.example.app.config.properties")
public class PropertyConfig {
}
