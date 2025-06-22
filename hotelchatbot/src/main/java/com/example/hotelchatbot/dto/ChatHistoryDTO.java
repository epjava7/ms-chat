package com.example.hotelchatbot.dto;

import java.util.List;

public class ChatHistoryDTO {
    private List<ChatMessageDTO> messages;
    private Long sessionId;

    public List<ChatMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDTO> messages) {
        this.messages = messages;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}