package com.example.supportservice.service;

import com.example.supportservice.dto.ChatMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

import com.example.supportservice.dto.ChatSessionDTO;

@Service
public class HotelChatbotClient {

    private String hotelChatbotBaseUrl = "http://localhost:8081";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<ChatMessageDTO> getChatHistory(Long sessionId) {
        String url = hotelChatbotBaseUrl + "/api/chatbot/session/" + sessionId + "/messages";
        ChatMessageDTO[] messages = restTemplate.getForObject(url, ChatMessageDTO[].class);
        return Arrays.asList(messages);
    }

    public List<ChatSessionDTO> getEscalatedSessions() {
        String url = hotelChatbotBaseUrl + "/api/chatbot/escalated-sessions";
        ChatSessionDTO[] sessions = restTemplate.getForObject(url, ChatSessionDTO[].class);
        return Arrays.asList(sessions);
    }
}