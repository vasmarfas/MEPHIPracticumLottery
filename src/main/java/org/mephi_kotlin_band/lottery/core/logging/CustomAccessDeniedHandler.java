package org.mephi_kotlin_band.lottery.core.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("⛔ Access denied handler triggered - method invoked");
        log.warn("⛔ Access denied for path: {} | Reason: {}", request.getRequestURI(), accessDeniedException.getMessage());
        var principal = request.getUserPrincipal();
        if (principal != null) {
            log.warn("⛔ Access denied for user: {}", principal.getName());
        } else {
            log.warn("⛔ Access denied - anonymous request");
            log.warn("⛔ Request method: {}, Remote addr: {}", request.getMethod(), request.getRemoteAddr());
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                  "error": "Access denied"
                }
                """);
    }
}