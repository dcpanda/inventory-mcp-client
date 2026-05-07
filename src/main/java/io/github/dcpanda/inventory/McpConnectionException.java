package io.github.dcpanda.inventory;

public class McpConnectionException extends RuntimeException {
    public McpConnectionException(String message) {
        super(message);
    }

    public McpConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
