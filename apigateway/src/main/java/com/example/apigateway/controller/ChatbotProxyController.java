package com.example.apigateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/chatbot/api")
public class ChatbotProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${chatbot.service.url}")
    private String chatbotServiceUrl;

    @Value("${support.service.url}")
    private String supportServiceUrl;

    @GetMapping("/escalated-sessions") 
    public ResponseEntity<?> getEscalatedSessions() {
        String url = supportServiceUrl + "/api/support/escalated-sessions";
        return restTemplate.getForEntity(url, Object.class);
    }

    @PostMapping("/chatbot/session")
    public ResponseEntity<?> createSessionProxy(HttpServletRequest request) {
        String url = chatbotServiceUrl + "/api/chatbot/session";
        HttpHeaders headers = new HttpHeaders();

        // Extract JWT from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            System.out.println("JWT Claims: " + jwt.getClaims());
            String username = jwt.getClaimAsString("preferred_username");
            if (username == null) {
                username = jwt.getClaimAsString("sub");
            }
            System.out.println("Forwarding username: " + username);
            if (username != null) {
                headers.add("X-User-Name", username);
            }
        }

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    }

    @GetMapping("/hotels")
    public ResponseEntity<?> getHotels(@RequestParam MultiValueMap<String, String> params) {
        String url = chatbotServiceUrl + "/api/hotels";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(params);
        var response = restTemplate.getForEntity(builder.toUriString(), Object.class);
        return response;
    }

    @PostMapping("/hotels/search-ai")
    public ResponseEntity<?> searchAI(@RequestBody Object request) {
        String url = chatbotServiceUrl + "/api/hotels/search-ai";
        return restTemplate.postForEntity(url, request, Object.class);
    }

    @PostMapping("/reply")
    public ResponseEntity<?> sendReply(@RequestParam String sessionId, @RequestBody String reply) {
        String url = supportServiceUrl + "/api/support/reply?sessionId=" + sessionId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(reply, headers);
        return restTemplate.postForEntity(url, entity, Object.class);
    }

    @GetMapping("/chatbot/session/{sessionId}/messages")
    public ResponseEntity<?> getSessionMessages(@PathVariable Long sessionId) {
        String url = chatbotServiceUrl + "/api/chatbot/session/" + sessionId + "/messages";
        return restTemplate.getForEntity(url, Object.class);
    }

    @PostMapping("/chatbot/session/{sessionId}/message")
    public ResponseEntity<?> sendMessageToSession(@PathVariable Long sessionId, @RequestBody String content) {
        String url = chatbotServiceUrl + "/api/chatbot/session/" + sessionId + "/message";
        return restTemplate.postForEntity(url, content, Object.class);
    }

    @DeleteMapping("/chatbot/session/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable Long sessionId) {
        String url = chatbotServiceUrl + "/api/chatbot/session/" + sessionId;
        restTemplate.delete(url);
        return ResponseEntity.ok().build();
    }
}