package com.example.supportservice.dto;

public class ChatSessionDTO {
    private Long id;
    private boolean escalated;
    private Long userId;
    private String username; 
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean isEscalated() {
        return escalated;
    }
    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
