package com.example.hotelchatbot.dto;

public class WebSocketMessageDTO {
    private String sessionId;
    private String content;
    private String sender; // user,agent,assistant

    public WebSocketMessageDTO() {}

    public WebSocketMessageDTO(String sessionId, String content, String sender) {
        this.sessionId = sessionId;
        this.content = content;
        this.sender = sender;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getSender() {
        return sender;
    }
}
