package io.github.dcpanda.inventory;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class InventoryTools {

    private final McpService mcpService;

    public InventoryTools(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @Tool(description = "List all available warehouse codes and their descriptions.")
    public Mono<List<String>> listWarehouses() {
        return mcpService.listResourceTemplates()
                .map(templates -> templates.stream()
                        .map(t -> t.name() + ": " + t.description())
                        .toList());
    }

    @Tool(description = "Retrieve detailed inventory resources for a specific warehouse (e.g., WH-001, WH-002). " +
                        "Use this when the user asks about the contents or stock of a specific warehouse.")
    public Mono<String> getWarehouseInventory(String warehouseCode) {
        return mcpService.getResource(warehouseCode);
    }
}
