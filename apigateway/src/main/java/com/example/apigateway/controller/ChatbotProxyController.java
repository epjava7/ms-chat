package com.example.apigateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/chatbot/api")
public class ChatbotProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${chatbot.service.url}")
    private String chatbotServiceUrl;

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
}