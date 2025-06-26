package com.example.hotelchatbot.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/escalation")
public class EscalationController {

    @GetMapping("/escalated-sessions")
    public List<String> getEscalatedSessions() {
        // Replace with your actual logic to fetch escalated sessions
        return List.of("session1", "session2");
    }
}