package com.example.hotelchatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.hotelchatbot.dto.WebSocketMessageDTO;

@Service
public class ChatWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessageToSession(String sessionId, String content, String sender) {
        WebSocketMessageDTO message = new WebSocketMessageDTO(sessionId, content, sender);
        messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
    }

    public void notifyAgentsOfEscalation(String sessionId) {
        messagingTemplate.convertAndSend("/topic/agents", 
            new WebSocketMessageDTO(sessionId, "NEW_ESCALATION", "system"));
    }
}