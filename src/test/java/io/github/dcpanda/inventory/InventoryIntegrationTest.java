package io.github.dcpanda.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.ai.mcp.client.enabled=false"
    }
)
@AutoConfigureWebTestClient
public class InventoryIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ChatModel chatModel;

    @MockitoBean
    private McpService mcpService;

    @Test
    void testChatIntegration() {
        ChatResponse mockResponse = new ChatResponse(List.of(new Generation(new AssistantMessage("I can help with inventory."))));
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        webTestClient.get().uri("/chat?message=SKU-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("I can help with inventory.");
    }
}
