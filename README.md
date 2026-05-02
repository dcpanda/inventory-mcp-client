# Inventory MCP Client

A Spring Boot application that serves as a Model Context Protocol (MCP) client, integrating with a legacy inventory system via Spring AI.
See the GitHub repository for [Inventory MCP Server](https://github.com/dcpanda/inventory-mcp-server) more details.

## Overview

This project demonstrates how to build a modern AI-integrated client that:
- Connects to an MCP Server using Server-Sent Events (SSE).
- Dynamically discovers tools and resources from the MCP server.
- Integrates discovered tools into a Spring AI `ChatClient` workflow.
- Enables natural language queries against legacy inventory data.

## Features

- **Dynamic Tool Discovery**: Automatically fetches and registers tools from the MCP server.
- **AI-Powered Chat**: An endpoint (`/chat`) that uses a `ChatClient` with tool-calling capabilities.
- **MCP Inspection**: Endpoints to inspect tools and resources available on the connected MCP server.
- **Reactive Stack**: Built on Spring Boot WebFlux for efficient, non-blocking communication.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- A running MCP Server (expected at `http://localhost:8080/mcp`). The MCP Server example used here can be found [here](https://github.com/dcpanda/inventory-mcp-server).
- An OpenAI-compatible LLM provider. I used LM Studio at `http://localhost:1234`. You can use ollama or any other LLM provider that supports the OpenAI API.

### Configuration

The application is configured via `src/main/resources/application.yml`. Key settings include:

```yaml
spring:
  ai:
    openai:
      base-url: http://localhost:1234/ # LLM provider URL
      api-key: lm-studio
    mcp:
      client:
        sse:
          connections:
            inventory-server:
              url: http://localhost:8080
              sse-endpoint: /mcp
```

### Running the Application

1. Start your MCP Server and LLM provider.
2. Run the client:

```bash
mvn spring-boot:run
```

The application will start on port `8081`. Did this to ensure that it does not conflict with the MCP Server.

## API Endpoints

### Chat
- `GET /chat?message={query}`: Interact with the AI agent.
  - **Default**: `http://localhost:8081/chat?message=What tools do you have access to?`
  - **Example 1**: `http://localhost:8081/chat?message=What are the warehouses available?`
  - **Example 2**: `http://localhost:8081/chat?message=Check stock for SKU-8842?`
  - **Example 3 (Spanish)**: `http://localhost:8081/chat?message=¿Cuál es el stock disponible para este producto SKU-8842?`

### MCP Inspection
- `GET /mcp/tools`: List all tools discovered from the MCP server.
- `GET /mcp/resources`: List all resource templates from the MCP server.
- `GET /mcp/resources/{warehouseCode}`: Read a specific inventory resource (e.g., `/mcp/resources/WH-001`). The Inventory MCP Server example uses a `warehouse` resource template and the README.md explains when to use Tools and Resources.

## Security: Prompt Injection Prevention

This application implements a multi-layered defense strategy to prevent prompt injection attacks, ensuring that the AI agent remains within its intended operational boundaries.

### 1. Multi-Layered Keyword Filtering
The `ChatClient` uses a `SafeGuardAdvisor` that intercepts common malicious patterns (e.g., "ignore previous instructions", "jailbreak"). By focusing on intent-based injection patterns rather than broad domain words, we ensure that legitimate inventory queries are not accidentally blocked while maintaining a strong defense against instruction overrides.

I have added unit testing to test different scenarios and ensure that the `SafeGuardAdvisor` is working as expected.

### 2. Strict Domain Enforcement with Few-Shot Examples
The system prompt has been reinforced with **Few-Shot Prompting**. By providing concrete examples of how to handle "mixed-intent" queries, the model learns to:
- Fulfill the legitimate inventory task.
- Explicitly refuse non-inventory tasks without repeating the forbidden instructions (to avoid triggering secondary safety filters). 
- Stay within its "Inventory Assistant" persona even under pressure.

### 3. Structural Input Separation and Sanitization
- **Tagging**: User input is isolated within `<user_query>` tags.
- **Sanitization**: The application now escapes XML characters (`<`, `>`) in user input to prevent "Tag Breakout" attacks where an attacker might try to close the `<user_query>` tag and inject new system instructions.
- **Instruction Reinforcement**: A reminder is added to every user message to treat the subsequent tagged content strictly as data.

### 4. Advisor-Based Security
By using Spring AI's `Advisor` API, security checks are decoupled from the core business logic, allowing for consistent enforcement across the entire chat workflow.

## MCP Resources Integration
Model Context Protocol (MCP) distinguishes between **Tools** (actions) and **Resources** (data). While tools are automatically discovered and registered with the `ChatClient`, resources require a bridge to be accessible by the LLM.

### Bridging Resources to Tools
In this example, we bridge MCP Resources to the LLM by creating an `InventoryTools` component. This component uses the `McpService` to fetch resource data and exposes it to the LLM via `@Tool` annotated methods:

1. **Listing Resources**: The `listWarehouses` tool allows the LLM to discover available warehouse codes from MCP resource templates.
2. **Reading Resources**: The `getWarehouseInventory` tool allows the LLM to read the detailed content of a specific warehouse resource (e.g., `inventory://warehouse/WH-001`).

### System Prompt Guidance
The LLM is instructed via the system prompt to use these tools whenever a user asks about warehouse-specific contents. This ensures that the LLM knows when and how to access the structured data provided by the MCP server's resources.

## Development

- **Main Application**: `io.github.dcpanda.inventory.InventoryClientApplication`
- **AI Logic**: `io.github.dcpanda.inventory.ChatController` handles the `ChatClient` integration.
- **MCP Client**: `io.github.dcpanda.inventory.McpController` manages direct interaction with the `McpService`.
- **MCP Service**: `io.github.dcpanda.inventory.McpService` encapsulates interaction with `McpAsyncClient`.

### Testing

There are three specific types of tests in this project – Prompt Security Tests, Unit Tests, and Integration Tests.

1. **Prompt Security Tests**:
   - Verifies the `SafeGuardAdvisor` configuration.
   - Ensures malicious keywords like "jailbreak" and "ignore previous instructions" are correctly blocked.
   - Confirms that legitimate inventory queries are allowed.

2. **Unit Tests**:
   - Tests the `/chat` REST endpoint with mocked AI dependencies.
   - Tests the MCP discovery endpoints with a mocked `McpService`. This required me to refactor the `McpService` to make it testable with Mockito.
   - Tests the bridge between MCP resources and LLM tools. Similar testing is also present in the MCP Server example.

3. **Integration Tests**:
   - Performs end-to-end testing of the application context.
   - Mocks external LLM and MCP server responses to verify full-stack integration. 
   
To run the tests:
```bash
mvn test
```
