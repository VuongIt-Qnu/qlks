package com.example.hotel.config;

import com.example.hotel.mongo.document.RequestLogDocument;
import com.example.hotel.mongo.repository.RequestLogRepository;
import com.example.hotel.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final RequestLogRepository requestLogRepository;

    public RequestLoggingFilter(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            try {
                RequestLogDocument doc = new RequestLogDocument();
                doc.setPath(request.getRequestURI());
                doc.setMethod(request.getMethod());
                doc.setStatus(response.getStatus());
                doc.setDurationMs(System.currentTimeMillis() - start);
                doc.setCreatedAt(Instant.now());
                if (SecurityContextHolder.getContext().getAuthentication() != null
                        && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                    try {
                        doc.setUserId(SecurityUtils.getCurrentUserId());
                    } catch (Exception ignored) {
                        // anonymous
                    }
                }
                requestLogRepository.save(doc);
            } catch (Exception e) {
                logger.warn("request log failed: {}", e.getMessage());
            }
        }
    }
}
