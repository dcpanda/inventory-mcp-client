package io.github.dcpanda.inventory;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InventoryToolsTest {

    @Test
    void testListWarehouses() {
        McpService mcpService = mock(McpService.class);
        McpSchema.ResourceTemplate template = new McpSchema.ResourceTemplate("uri-template", "WH-001", "Main Warehouse", "mime-type", null);
        
        when(mcpService.listResourceTemplates()).thenReturn(Mono.just(List.of(template)));
        
        InventoryTools tools = new InventoryTools(mcpService);
        Mono<List<String>> warehousesMono = tools.listWarehouses();
        
        StepVerifier.create(warehousesMono)
                .assertNext(warehouses -> assertThat(warehouses).contains("WH-001: Main Warehouse"))
                .verifyComplete();
    }

    @Test
    void testGetWarehouseInventory() {
        McpService mcpService = mock(McpService.class);
        when(mcpService.getResource("WH-001")).thenReturn(Mono.just("Inventory: SKU-123 x 10"));
        
        InventoryTools tools = new InventoryTools(mcpService);
        Mono<String> inventoryMono = tools.getWarehouseInventory("WH-001");
        
        StepVerifier.create(inventoryMono)
                .assertNext(inventory -> assertThat(inventory).isEqualTo("Inventory: SKU-123 x 10"))
                .verifyComplete();
    }
}
