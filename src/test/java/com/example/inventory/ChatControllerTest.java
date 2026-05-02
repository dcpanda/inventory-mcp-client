package com.example.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatControllerTest {

    private ChatClient chatClient;
    private ChatClient.Builder builder;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        chatClient = mock(ChatClient.class);
        builder = mock(ChatClient.Builder.class, Answers.RETURNS_SELF);
        when(builder.build()).thenReturn(chatClient);
        InventoryTools inventoryTools = mock(InventoryTools.class);

        ChatController chatController = new ChatController(builder, List.of(), inventoryTools);
        webTestClient = WebTestClient.bindToController(chatController).build();
    }

    @Test
    void testChatEndpoint() {
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class, Answers.RETURNS_SELF);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Mocked response");

        webTestClient.get().uri("/chat?message=hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Mocked response");
    }
}
