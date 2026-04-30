package com.example.inventory;

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

    private final McpAsyncClient mcpAsyncClient;

    public McpController(List<McpAsyncClient> mcpAsyncClients) {
        System.out.println("Number of MCP clients: " + mcpAsyncClients.size());
        this.mcpAsyncClient = mcpAsyncClients.isEmpty() ? null : mcpAsyncClients.get(0);
    }

    @GetMapping("/tools")
    public Mono<List<McpSchema.Tool>> listTools() {
        if (mcpAsyncClient == null) {
            return Mono.error(new RuntimeException("No MCP client available"));
        }
        return mcpAsyncClient.listTools(null).map(McpSchema.ListToolsResult::tools);
    }

    @GetMapping("/resources")
    public Mono<List<McpSchema.ResourceTemplate>> listResourceTemplates() {
        return mcpAsyncClient.listResourceTemplates(null).map(McpSchema.ListResourceTemplatesResult::resourceTemplates);
    }

    @GetMapping("/resources/{warehouseCode}")
    public Mono<String> getResource(@PathVariable String warehouseCode) {
        var resourceUri = "inventory://warehouse/" + warehouseCode;
        return mcpAsyncClient.readResource(new McpSchema.ReadResourceRequest(resourceUri))
                .map(response -> {
                    var content = response.contents().get(0);
                    if (content instanceof McpSchema.TextResourceContents textContent) {
                        return textContent.text();
                    }
                    return "Not a text resource";
                });
    }
}
