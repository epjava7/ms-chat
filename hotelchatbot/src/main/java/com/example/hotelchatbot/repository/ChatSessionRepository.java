package com.example.hotelchatbot.repository;

import com.example.hotelchatbot.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    // List<ChatSession> findByUserId(Long userId);
    List<ChatSession> findByUsername(String username);
    List<ChatSession> findByEscalatedTrue();
}