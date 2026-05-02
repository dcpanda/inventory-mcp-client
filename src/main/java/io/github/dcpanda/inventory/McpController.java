package io.github.dcpanda.inventory;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final McpService mcpService;

    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @GetMapping("/tools")
    public Mono<List<McpSchema.Tool>> listTools() {
        return mcpService.listTools();
    }

    @GetMapping("/resources")
    public Mono<List<McpSchema.ResourceTemplate>> listResourceTemplates() {
        return mcpService.listResourceTemplates();
    }

    @GetMapping("/resources/{warehouseCode}")
    public Mono<String> getResource(@PathVariable String warehouseCode) {
        return mcpService.getResource(warehouseCode);
    }
}
