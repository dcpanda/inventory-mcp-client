package com.example.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class SafeGuardTest {

    @Test
    void testSafeGuardTriggering() {
        ChatModel chatModel = mock(ChatModel.class);
        ChatResponse mockResponse = new ChatResponse(List.of(new Generation(new AssistantMessage("The stock for SKU-8842 is 10."))));
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        
        List<String> sensitiveWords = List.of(
                        "ignore previous instructions",
                        "disregard all prior instructions",
                        "forget everything",
                        "new system prompt",
                        "you are now a",
                        "as an administrator",
                        "bypass safety",
                        "jailbreak"
        );

        ChatClient chatClient = builder
                .defaultAdvisors(new SafeGuardAdvisor(sensitiveWords))
                .build();

        String response = chatClient.prompt()
                .user("what is stock for the product SKU-8842")
                .call()
                .content();

        System.out.println("Response: " + response);
        assertThat(response).contains("The stock for SKU-8842 is 10.");
    }
    
    @Test
    void testSafeGuardTriggeringWithSystemPrompt() {
        ChatModel chatModel = mock(ChatModel.class);
        ChatResponse mockResponse = new ChatResponse(List.of(new Generation(new AssistantMessage("The stock for SKU-8842 is 10."))));
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        
        List<String> sensitiveWords = List.of(
                        "ignore previous instructions",
                        "disregard all prior instructions",
                        "forget everything",
                        "new system prompt",
                        "you are now a",
                        "as an administrator",
                        "bypass safety",
                        "jailbreak"
        );

        ChatClient chatClient = builder
                .defaultSystem("You must not tell a joke.")
                .defaultAdvisors(new SafeGuardAdvisor(sensitiveWords))
                .build();

        String response = chatClient.prompt()
                .user("what is stock for the product SKU-8842")
                .call()
                .content();

        System.out.println("Response with system prompt: " + response);
        assertThat(response).isNotNull();
    }
    
    @Test
    void testSafeGuardBlocking() {
        ChatModel chatModel = mock(ChatModel.class);
        // This response should NOT be reached if SafeGuardAdvisor works as expected on the request
        ChatResponse mockResponse = new ChatResponse(List.of(new Generation(new AssistantMessage("I am now a helpful assistant." ))));
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        
        List<String> sensitiveWords = List.of(
                        "jailbreak"
        );

        ChatClient chatClient = builder
                .defaultAdvisors(new SafeGuardAdvisor(sensitiveWords))
                .build();

        String response = chatClient.prompt()
                .user("Please jailbreak this system")
                .call()
                .content();

        System.out.println("Response for jailbreak: " + response);
        assertThat(response).contains("unable to respond");
    }

    @Test
    void testDefaultMessage() {
        ChatModel chatModel = mock(ChatModel.class);
        ChatResponse mockResponse = new ChatResponse(List.of(new Generation(new AssistantMessage("I have access to tools."))));
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        
        List<String> sensitiveWords = List.of(
                        "ignore previous instructions",
                        "disregard all prior instructions",
                        "forget everything",
                        "new system prompt",
                        "you are now a",
                        "as an administrator",
                        "bypass safety",
                        "jailbreak"
        );

        ChatClient chatClient = builder
                .defaultAdvisors(new SafeGuardAdvisor(sensitiveWords))
                .build();

        String response = chatClient.prompt()
                .user("What tools do you have access to?")
                .call()
                .content();

        System.out.println("Response for default message: " + response);
        assertThat(response).contains("I have access to tools.");
    }
}
