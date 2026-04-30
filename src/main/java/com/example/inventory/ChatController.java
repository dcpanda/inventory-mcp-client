package com.example.inventory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder, List<ToolCallbackProvider> toolCallbackProviders) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProviders.toArray(new ToolCallbackProvider[0]))
                .build();
    }

    @GetMapping("/chat")
    public Mono<String> chat(@RequestParam(value = "message", defaultValue = "What tools do you have access to?") String message) {
        return Mono.just(chatClient.prompt()
                .user(message)
                .call()
                .content());
    }
}
