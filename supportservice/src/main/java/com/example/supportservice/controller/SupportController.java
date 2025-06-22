package com.example.supportservice.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.supportservice.service.HotelChatbotClient;
import com.example.supportservice.dto.ChatMessageDTO;
import com.example.supportservice.service.SupportChatService;
import com.example.supportservice.dto.ChatSessionDTO;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportChatService chatService;
    private final HotelChatbotClient hotelChatbotClient;

    public SupportController(SupportChatService chatService, HotelChatbotClient hotelChatbotClient) {
        this.chatService = chatService;
        this.hotelChatbotClient = hotelChatbotClient;
    }

    // send reply from agent
    @PostMapping("/reply")
    public void sendReply(@RequestParam String sessionId, @RequestBody String reply) {
        chatService.sendAgentReply(sessionId, reply);
    }

    @GetMapping("/session/{sessionId}/messages")
    public List<ChatMessageDTO> getSessionMessages(@PathVariable Long sessionId) {
        return hotelChatbotClient.getChatHistory(sessionId);
    }

    // frontend calls this to get escalated sessions
    @GetMapping("/escalated-sessions")
    public List<ChatSessionDTO> getEscalatedSessions() {
        return hotelChatbotClient.getEscalatedSessions();
    }
}
