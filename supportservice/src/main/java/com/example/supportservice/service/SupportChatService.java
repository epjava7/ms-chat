package com.example.supportservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SupportChatService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "support-chat", groupId = "support-agents")
    public void listenUserMessage(String message) {
        // do this: display message for agent UI
        System.out.println("Received from user: " + message);
    }

    public void sendAgentReply(String sessionId, String reply) {
        kafkaTemplate.send("support-chat-replies", sessionId, reply);
    }
}