package io.github.dcpanda.inventory;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

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
        List<String> warehouses = tools.listWarehouses();
        
        assertThat(warehouses).contains("WH-001: Main Warehouse");
    }

    @Test
    void testGetWarehouseInventory() {
        McpService mcpService = mock(McpService.class);
        when(mcpService.getResource("WH-001")).thenReturn(Mono.just("Inventory: SKU-123 x 10"));
        
        InventoryTools tools = new InventoryTools(mcpService);
        String inventory = tools.getWarehouseInventory("WH-001");
        
        assertThat(inventory).isEqualTo("Inventory: SKU-123 x 10");
    }
}
