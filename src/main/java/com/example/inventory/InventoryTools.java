package com.example.inventory;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryTools {

    private final McpService mcpService;

    public InventoryTools(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @Tool(description = "List all available warehouse codes and their descriptions.")
    public List<String> listWarehouses() {
        return mcpService.listResourceTemplates()
                .map(templates -> templates.stream()
                        .map(t -> t.name() + ": " + t.description())
                        .toList())
                .block();
    }

    @Tool(description = "Retrieve detailed inventory resources for a specific warehouse (e.g., WH-001, WH-002). " +
                        "Use this when the user asks about the contents or stock of a specific warehouse.")
    public String getWarehouseInventory(String warehouseCode) {
        // The McpService returns a Mono, but standard tool execution in Spring AI 1.1.x
        // often expects a synchronous return when using @Tool on a bean.
        return mcpService.getResource(warehouseCode).block();
    }
}
