package org.mephi_kotlin_band.lottery.core.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String authHeader = request.getHeader("Authorization");
        String tokenType = (authHeader != null && authHeader.startsWith("Bearer")) ? "Bearer token" : "No token";

        log.warn("""
            ❌ Unauthorized access attempt:
              • URI         : {} {}
              • IP          : {}
              • Token header: {}
              • User-Agent  : {}
              • Reason      : {}
            """, method, uri, ip, tokenType, userAgent, authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
        {
          "error": "Unauthorized"
        }
        """);
    }
}