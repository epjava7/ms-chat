package com.example.hotelchatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

import com.example.hotelchatbot.service.ChatbotAIService;
import com.example.hotelchatbot.domain.ChatSession;
import com.example.hotelchatbot.dto.ChatHistoryDTO;
import com.example.hotelchatbot.repository.ChatMessageRepository;
import com.example.hotelchatbot.repository.ChatSessionRepository;
import com.example.hotelchatbot.domain.ChatMessage;
import com.example.hotelchatbot.service.SupportChatKafkaService;
import com.example.hotelchatbot.repository.UserRepository;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:3001")
public class ChatbotController {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatbotAIService chatbotAIService;
    private final SupportChatKafkaService supportChatKafkaService;
    private final UserRepository userRepository;

    public ChatbotController(
        ChatSessionRepository chatSessionRepository,
        ChatMessageRepository chatMessageRepository,
        ChatbotAIService chatbotAIService,
        SupportChatKafkaService supportChatKafkaService,
        UserRepository userRepository
    ) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatbotAIService = chatbotAIService;
        this.supportChatKafkaService = supportChatKafkaService;
        this.userRepository = userRepository;
    }

    @GetMapping("/escalated-sessions")
    public List<ChatSession> getEscalatedSessions() {
        return chatSessionRepository.findByEscalatedTrue();
    }

    @PostMapping("/session")
    public ChatSession createSession() {
        ChatSession session = new ChatSession();
        session.setEscalated(false);
        return chatSessionRepository.save(session);
    }

    @GetMapping("/session/{sessionId}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long sessionId) {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    // send message to openAI -> getAI reply -> save both
    @PostMapping("/session/{sessionId}/message")
    public ChatMessage sendMessage(@PathVariable Long sessionId, @RequestBody String content) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        ChatMessage userMsg = new ChatMessage();
        userMsg.setSession(session);
        userMsg.setSender("user");
        userMsg.setContent(content);
        userMsg.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(userMsg);

        // support via kafka
        if (session.isEscalated()) {
            supportChatKafkaService.sendToSupport(sessionId.toString(), content);

            boolean alreadyTransferred = chatMessageRepository
                .findBySessionIdOrderByTimestampAsc(sessionId)
                .stream()
                .anyMatch(msg -> 
                    "assistant".equals(msg.getSender()) && 
                    "transferring to human agent...".equals(msg.getContent())
                );

            if (!alreadyTransferred) {
                ChatMessage placeholder = new ChatMessage();
                placeholder.setSession(session);
                placeholder.setSender("assistant");
                placeholder.setContent("transferring to human agent...");
                placeholder.setTimestamp(LocalDateTime.now());
                chatMessageRepository.save(placeholder);
                return placeholder;
            } else {
                // latest agent or assistant message
                List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
                for (int i = messages.size() - 1; i >= 0; i--) {
                    ChatMessage msg = messages.get(i);
                    if ("assistant".equals(msg.getSender()) || "agent".equals(msg.getSender())) {
                        return msg;
                    }
                }
                return messages.get(messages.size() - 1);
            }
        }

        // build chat history, call AI
        List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
        String aiReply = chatbotAIService.askOpenAiFromEntities(history);

        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setSession(session);
        aiMsg.setSender("assistant");
        aiMsg.setContent(aiReply);
        aiMsg.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(aiMsg);

        return aiMsg;
    }

    @DeleteMapping("/session/{sessionId}")
    public void deleteSession(@PathVariable Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        chatSessionRepository.delete(session);
    }

    // test endpoint
    @PostMapping("/query")
    public String handleQuery(@RequestBody ChatHistoryDTO chatHistoryDTO) {
        return chatbotAIService.askOpenAi(chatHistoryDTO);
    }
}
