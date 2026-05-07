package io.github.dcpanda.inventory;

public class McpResourceAccessException extends RuntimeException {
    public McpResourceAccessException(String message) {
        super(message);
    }

    public McpResourceAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
