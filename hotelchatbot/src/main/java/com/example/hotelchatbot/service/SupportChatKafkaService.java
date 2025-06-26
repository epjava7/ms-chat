package com.example.hotelchatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import com.example.hotelchatbot.domain.ChatSession;
import com.example.hotelchatbot.domain.ChatMessage;
import com.example.hotelchatbot.repository.ChatSessionRepository;
import com.example.hotelchatbot.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;


@Service
public class SupportChatKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatWebSocketService chatWebSocketService;

    public SupportChatKafkaService(
        KafkaTemplate<String, String> kafkaTemplate,
        ChatSessionRepository chatSessionRepository,
        ChatMessageRepository chatMessageRepository,
        ChatWebSocketService chatWebSocketService
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatWebSocketService = chatWebSocketService;
    }

    public void sendToSupport(String sessionId, String message) {
        kafkaTemplate.send("support-chat", sessionId, message);
    }

    @KafkaListener(topics = "support-chat-replies", groupId = "hotelchatbot")
    public void receiveAgentReply(String message, 
        @Header(KafkaHeaders.RECEIVED_KEY) String sessionId) {
        ChatSession session = chatSessionRepository.findById(Long.valueOf(sessionId)).orElse(null);
        if (session != null) {
            ChatMessage agentMsg = new ChatMessage();
            agentMsg.setSession(session);
            agentMsg.setSender("agent");
            agentMsg.setContent(message);
            agentMsg.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(agentMsg);

            chatWebSocketService.sendMessageToSession(sessionId, message, "agent");
        }
    }
    
}
