package com.example.app.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class MdcLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            final var xRequestId = ((HttpServletRequest) request).getHeader("X-Request-ID");
            MDC.put("request_id", xRequestId == null ? UUID.randomUUID().toString() : xRequestId);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
