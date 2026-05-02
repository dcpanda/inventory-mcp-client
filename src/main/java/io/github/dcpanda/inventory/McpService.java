package io.github.dcpanda.inventory;

import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;

import java.util.List;

public interface McpService {
    Mono<List<McpSchema.Tool>> listTools();
    Mono<List<McpSchema.ResourceTemplate>> listResourceTemplates();
    Mono<String> getResource(String warehouseCode);
}
