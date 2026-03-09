package com.example.app.config;

import com.example.app.common.filter.MdcLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingFilterConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public MdcLoggingFilter mdcLoggingFilter() {
        return new MdcLoggingFilter();
    }

    @Bean
    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
        final var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        return filter;
    }
}
