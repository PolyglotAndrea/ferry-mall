package com.ferry.framework.web.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(30)
public class HttpServerMetricsFilter extends OncePerRequestFilter {
    private final MeterRegistry meterRegistry;

    public HttpServerMetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            String uri = normalizeUri(request.getRequestURI());
            String status = Integer.toString(response.getStatus());
            meterRegistry.counter("ferry.http.server.requests.total", "method", request.getMethod(), "uri", uri, "status", status).increment();
            Timer.builder("ferry.http.server.requests.duration")
                .description("HTTP server request duration")
                .tag("method", request.getMethod())
                .tag("uri", uri)
                .tag("status", status)
                .register(meterRegistry)
                .record(Duration.ofNanos(System.nanoTime() - start));
        }
    }

    private String normalizeUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return "UNKNOWN";
        }
        return uri.replaceAll("/\\d+", "/{id}");
    }
}
