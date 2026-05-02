package io.github.dcpanda.inventory;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class McpControllerTest {

    private McpService mcpService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        mcpService = Mockito.mock(McpService.class);
        McpController mcpController = new McpController(mcpService);
        webTestClient = WebTestClient.bindToController(mcpController).build();
    }

    @Test
    void testListTools() {
        McpSchema.Tool tool = mock(McpSchema.Tool.class);
        when(tool.name()).thenReturn("test-tool");
        
        when(mcpService.listTools()).thenReturn(Mono.just(List.of(tool)));

        webTestClient.get().uri("/mcp/tools")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("test-tool");
    }

    @Test
    void testListResources() {
        McpSchema.ResourceTemplate template = new McpSchema.ResourceTemplate("uri-template", "resource-name", "description", "mime-type", null);

        when(mcpService.listResourceTemplates()).thenReturn(Mono.just(List.of(template)));

        webTestClient.get().uri("/mcp/resources")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("resource-name");
    }
}
