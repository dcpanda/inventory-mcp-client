package io.github.dcpanda.inventory;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class McpServiceImpl implements McpService {

    private final McpAsyncClient mcpAsyncClient;

    public McpServiceImpl(List<McpAsyncClient> mcpAsyncClients) {
        this.mcpAsyncClient = mcpAsyncClients.isEmpty() ? null : mcpAsyncClients.get(0);
    }

    @Override
    public Mono<List<McpSchema.Tool>> listTools() {
        if (mcpAsyncClient == null) {
            return Mono.error(new McpConnectionException("No MCP client available"));
        }
        return mcpAsyncClient.listTools(null)
                .map(McpSchema.ListToolsResult::tools)
                .onErrorMap(e -> !(e instanceof McpConnectionException),
                        e -> new McpResourceAccessException("Failed to list tools", e));
    }

    @Override
    public Mono<List<McpSchema.ResourceTemplate>> listResourceTemplates() {
        if (mcpAsyncClient == null) {
            return Mono.error(new McpConnectionException("No MCP client available"));
        }
        return mcpAsyncClient.listResourceTemplates(null)
                .map(McpSchema.ListResourceTemplatesResult::resourceTemplates)
                .onErrorMap(e -> !(e instanceof McpConnectionException),
                        e -> new McpResourceAccessException("Failed to list resource templates", e));
    }

    @Override
    public Mono<String> getResource(String warehouseCode) {
        if (mcpAsyncClient == null) {
            return Mono.error(new McpConnectionException("No MCP client available"));
        }
        var resourceUri = "inventory://warehouse/" + warehouseCode;
        return mcpAsyncClient.readResource(new McpSchema.ReadResourceRequest(resourceUri))
                .map(response -> {
                    var content = response.contents().get(0);
                    if (content instanceof McpSchema.TextResourceContents textContent) {
                        return textContent.text();
                    }
                    return "Not a text resource";
                })
                .onErrorMap(e -> !(e instanceof McpConnectionException),
                        e -> new McpResourceAccessException("Failed to read resource: " + warehouseCode, e));
    }
}
