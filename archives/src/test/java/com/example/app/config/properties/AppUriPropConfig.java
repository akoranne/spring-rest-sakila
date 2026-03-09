package com.example.app.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.uri")
@Getter
@ToString
@RequiredArgsConstructor
public class AppUriPropConfig {
    private final String scheme;
    private final String host;
    private final Integer port;
}
