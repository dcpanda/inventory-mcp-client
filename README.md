# Inventory MCP Client

A Spring Boot application that serves as a Model Context Protocol (MCP) client, integrating with a legacy inventory system via Spring AI.

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
- A running MCP Server (expected at `http://localhost:8080/mcp`)
- An OpenAI-compatible LLM provider (e.g., LM Studio at `http://localhost:1234`)

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

The application will start on port `8081`.

## API Endpoints

### Chat
- `GET /chat?message={query}`: Interact with the AI agent.
  - **Example**: `http://localhost:8081/chat?message=What tools do you have access to?`

### MCP Inspection
- `GET /mcp/tools`: List all tools discovered from the MCP server.
- `GET /mcp/resources`: List all resource templates from the MCP server.
- `GET /mcp/resources/{warehouseCode}`: Read a specific inventory resource (e.g., `/mcp/resources/WH-001`).

## Development

- **Main Application**: `com.example.inventory.InventoryClientApplication`
- **AI Logic**: `com.example.inventory.ChatController` handles the `ChatClient` integration.
- **MCP Client**: `com.example.inventory.McpController` manages direct interaction with the `McpAsyncClient`.
