package org.an.springai.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

public abstract class Agent {
    String conversationIdPrefix;
    ChatClient chatClient;
    ChatMemory chatMemory;
    String SYSTEM_PROMPT;
    public abstract String execute(String userPrompt);

    public Agent(){}

    public Agent(String conversationIdPrefix, ChatClient chatClient, ChatMemory chatMemory) {
        this.conversationIdPrefix = conversationIdPrefix;
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }
    public String getConversationIdPrefix() {
        return conversationIdPrefix;
    }

    public void setConversationIdPrefix(String conversationIdPrefix) {
        this.conversationIdPrefix = conversationIdPrefix;
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatMemory getChatMemory() {
        return chatMemory;
    }

    public void setChatMemory(ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
    }

    public String getSYSTEM_PROMPT() {
        return SYSTEM_PROMPT;
    }

    public void setSYSTEM_PROMPT(String SYSTEM_PROMPT) {
        this.SYSTEM_PROMPT = SYSTEM_PROMPT;
    }
}
