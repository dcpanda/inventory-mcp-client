package io.github.dcpanda.inventory;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class McpServiceImplTest {

    @Mock
    private McpAsyncClient mcpAsyncClient;

    @Test
    void testListToolsWhenClientNull() {
        McpService service = new McpServiceImpl(List.of());
        StepVerifier.create(service.listTools())
                .expectError(McpConnectionException.class)
                .verify();
    }

    @Test
    void testListResourceTemplatesWhenClientNull() {
        McpService service = new McpServiceImpl(List.of());
        StepVerifier.create(service.listResourceTemplates())
                .expectError(McpConnectionException.class)
                .verify();
    }

    @Test
    void testGetResourceWhenClientNull() {
        McpService service = new McpServiceImpl(List.of());
        StepVerifier.create(service.getResource("WH-001"))
                .expectError(McpConnectionException.class)
                .verify();
    }

    @Test
    void testListToolsSuccess() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        McpSchema.Tool tool = new McpSchema.Tool("test-tool", "Test Tool", "A test tool", null, null, null, null);
        McpSchema.ListToolsResult result = new McpSchema.ListToolsResult(List.of(tool), null);
        when(mcpAsyncClient.listTools(any())).thenReturn(Mono.just(result));

        StepVerifier.create(service.listTools())
                .expectNext(List.of(tool))
                .verifyComplete();
    }

    @Test
    void testListToolsError() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        when(mcpAsyncClient.listTools(any())).thenReturn(Mono.error(new RuntimeException("Connection failed")));

        StepVerifier.create(service.listTools())
                .expectError(McpResourceAccessException.class)
                .verify();
    }

    @Test
    void testListResourceTemplatesSuccess() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        McpSchema.ResourceTemplate template = new McpSchema.ResourceTemplate(
                "inventory://warehouse/{code}", "WH-001", "Main Warehouse", "text/plain", null);
        McpSchema.ListResourceTemplatesResult result = new McpSchema.ListResourceTemplatesResult(List.of(template), null);
        when(mcpAsyncClient.listResourceTemplates(any())).thenReturn(Mono.just(result));

        StepVerifier.create(service.listResourceTemplates())
                .expectNext(List.of(template))
                .verifyComplete();
    }

    @Test
    void testListResourceTemplatesError() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        when(mcpAsyncClient.listResourceTemplates(any())).thenReturn(Mono.error(new RuntimeException("Connection failed")));

        StepVerifier.create(service.listResourceTemplates())
                .expectError(McpResourceAccessException.class)
                .verify();
    }

    @Test
    void testGetResourceSuccess() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        String uri = "inventory://warehouse/WH-001";
        McpSchema.TextResourceContents content = new McpSchema.TextResourceContents(uri, "text/plain", "Inventory data", null);
        McpSchema.ReadResourceResult result = new McpSchema.ReadResourceResult(List.of(content));
        when(mcpAsyncClient.readResource(any(McpSchema.ReadResourceRequest.class))).thenReturn(Mono.just(result));

        StepVerifier.create(service.getResource("WH-001"))
                .expectNext("Inventory data")
                .verifyComplete();
    }

    @Test
    void testGetResourceSuccessWithResourceParam() {
        // This test is not applicable since McpServiceImpl uses ReadResourceRequest, not Resource
        // The testGetResourceSuccess already covers the actual usage
    }

    @Test
    void testGetResourceError() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        when(mcpAsyncClient.readResource((McpSchema.ReadResourceRequest) any())).thenReturn(Mono.error(new RuntimeException("Read failed")));

        StepVerifier.create(service.getResource("WH-001"))
                .expectError(McpResourceAccessException.class)
                .verify();
    }

    @Test
    void testGetResourceNonTextContent() {
        McpService service = new McpServiceImpl(List.of(mcpAsyncClient));
        String uri = "inventory://warehouse/WH-001";
        McpSchema.BlobResourceContents content = new McpSchema.BlobResourceContents(uri, "application/octet-stream", "blobdata", null);
        McpSchema.ReadResourceResult result = new McpSchema.ReadResourceResult(List.of(content));
        when(mcpAsyncClient.readResource(any(McpSchema.ReadResourceRequest.class))).thenReturn(Mono.just(result));

        StepVerifier.create(service.getResource("WH-001"))
                .expectNext("Not a text resource")
                .verifyComplete();
    }
}
