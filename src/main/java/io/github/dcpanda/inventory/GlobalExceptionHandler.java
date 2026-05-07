package io.github.dcpanda.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(McpConnectionException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleMcpConnectionException(McpConnectionException ex) {
        return new ErrorResponse("MCP_CONNECTION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(McpResourceAccessException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleMcpResourceAccessException(McpResourceAccessException ex) {
        return new ErrorResponse("MCP_RESOURCE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("INVALID_REQUEST", ex.getMessage());
    }

    public record ErrorResponse(String code, String message) {
    }
}
