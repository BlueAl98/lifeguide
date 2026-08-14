package com.nayibit.lifeguide.common.config;

import tools.jackson.databind.ObjectMapper;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.common.presentation.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Security rejects requests in the filter chain, before they ever
 * reach a controller — so {@code GlobalExceptionHandler}'s
 * {@code @ExceptionHandler}s never see them (those only run for exceptions
 * thrown inside the DispatcherServlet). Without this, a filter-chain denial
 * falls back to Spring Security's default entry point/handler, which just
 * sets the status code and writes an empty body. This class plugs the same
 * {@link ErrorResponse} JSON shape into both filter-chain rejection points:
 * {@link #commence} for "not authenticated" (401), {@link #handle} for
 * "authenticated but not allowed" (403).
 */
@Component
public class SecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public SecurityErrorHandlers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        write(response, ErrorCode.UNAUTHORIZED, request.getRequestURI());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        write(response, ErrorCode.FORBIDDEN, request.getRequestURI());
    }

    private void write(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        HttpStatus status = errorCode.getHttpStatus();
        ErrorResponse body = ErrorResponse.of(errorCode.name(), errorCode.getDefaultMessage(), status.value(), path);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
