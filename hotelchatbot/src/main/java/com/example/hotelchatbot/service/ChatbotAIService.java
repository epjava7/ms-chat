package com.example.hotelchatbot.service;

import org.springframework.ai.chat.messages.ChatMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Map;

import com.example.hotelchatbot.domain.ChatSession;
import com.example.hotelchatbot.dto.ChatHistoryDTO;
import com.example.hotelchatbot.dto.ChatMessageDTO;
import com.example.hotelchatbot.dto.HotelDTO;
import com.example.hotelchatbot.repository.ChatSessionRepository;

@Service
public class ChatbotAIService {

    @Autowired
    private FaqService faqService;
    
    @Autowired
    private IntentDetectionService intentDetectionService;

    @Autowired
    private OpenAiChatClient openAiChatClient;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @CircuitBreaker(name = "openAiCB", fallbackMethod = "fallbackOpenAi")
    public String askOpenAi(ChatHistoryDTO chatHistoryDTO) {
        List<ChatMessageDTO> messageDTOs = chatHistoryDTO.getMessages();

        // dto to chatmessages with roles
        List<ChatMessage> chatMessages = new java.util.ArrayList<>();
        for (ChatMessageDTO dto : messageDTOs) {
            MessageType type;
            if ("assistant".equalsIgnoreCase(dto.getRole())) {
                type = MessageType.ASSISTANT;
            } else {
                type = MessageType.USER;
            }
            chatMessages.add(new ChatMessage(type, dto.getContent()));
        }

        // intent detection 
        ChatMessageDTO latestMessageDTO = messageDTOs.get(messageDTOs.size() - 1);
        String latestMessage = latestMessageDTO.getContent();
        List<String> historyContents = new java.util.ArrayList<>();
        for (ChatMessageDTO dto : messageDTOs) {
            historyContents.add(dto.getContent());
        }
        String intent = intentDetectionService.detectIntent(latestMessage, historyContents);

        switch(intent) {

            case "FAQ":
                String hotelName = extractHotelName(latestMessage);
                if (hotelName == null) {
                    for (int i = messageDTOs.size() - 2; i >= 0; i--) {
                        hotelName = extractHotelName(messageDTOs.get(i).getContent());
                        if (hotelName != null) break;
                    }   
                }

                Integer hotelId = null;
                if (hotelName != null) {
                    for (var hotel : hotelService.getAllHotels()) {
                        if (hotel.getName().equalsIgnoreCase(hotelName)) {
                            hotelId = hotel.getId();
                            break;
                        }
                    }
                }

                if (hotelId != null) {
                    String matchedAnswer = faqService.findClosestAnswer(latestMessage, Long.valueOf(hotelId));
                    if (matchedAnswer != null) {
                        StringBuilder history = new StringBuilder();
                        for (ChatMessage msg : chatMessages) {
                            if (msg.getMessageType() == MessageType.USER) {
                                history.append("User: ");
                            } else {
                                history.append("Assistant: ");
                            }
                            history.append(msg.getContent()).append("\n");
                        }
                        String prompt = """
                            %s
                            User asked: "%s"
                            Relevant information: "%s"
                            Answer helpfully.
                            """.formatted(history.toString(), latestMessage, matchedAnswer);
                        return openAiChatClient.call(prompt);
                    }
                }
                break;

            case "HOTEL_SEARCH": {
                String prompt = """
                    Extract these from the user request: 
                    - city
                    - starRating (1-5)
                    - minPrice
                    - maxPrice
                    - amenities (comma separated)
                    - roomType
                    - guests
                    Return null if a field is not mentioned.
                    Respond in JSON format. 
                    User request: "%s"
                    """.formatted(latestMessage);

                String aiResponse = openAiChatClient.call(prompt);
                Map<String, Object> criteria = null;
                try {
                    criteria = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(aiResponse, Map.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String city = null;
                String roomType = null;
                Double minPrice = null;
                Double maxPrice = null;
                Integer guests = null;
                String amenities = null;
                Integer starRating = null;

                if (criteria != null) {
                    city = normalizeCityWithAI((String) criteria.get("city"));
                    roomType = (String) criteria.get("roomType");
                    amenities = (String) criteria.get("amenities");

                    Object minPriceObj = criteria.get("minPrice");
                    if (minPriceObj != null) minPrice = Double.valueOf(minPriceObj.toString());

                    Object maxPriceObj = criteria.get("maxPrice");
                    if (maxPriceObj != null) maxPrice = Double.valueOf(maxPriceObj.toString());

                    Object guestsObj = criteria.get("guests");
                    if (guestsObj != null) guests = Integer.valueOf(guestsObj.toString());

                    Object starRatingObj = criteria.get("starRating");
                    if (starRatingObj != null) starRating = Integer.valueOf(starRatingObj.toString());
                }

                List<HotelDTO> hotels = hotelService.getHotels(city, roomType, minPrice, maxPrice, guests, null, starRating, amenities);

                if (hotels.isEmpty()) {
                    return "No hotels found" + (city != null ? " in " + city : "");
                }
                StringBuilder sb = new StringBuilder("Available hotels");
                if (city != null) sb.append(" in ").append(city);
                sb.append(":\n");
                for (HotelDTO dto : hotels) {
                    sb.append("- ").append(dto.getName()).append(" (").append(dto.getCity()).append(")\n");
                }
                return sb.toString();
            }

            case "ESCALATE":
                ChatSession session = chatSessionRepository.findById(chatHistoryDTO.getSessionId()).orElse(null);
                if (session != null) {
                    if (session.getUser() == null) {
                        return "Log in to talk to a human agent";
                    }
                    session.setEscalated(true);
                    chatSessionRepository.save(session);
                }
                return "transferring to human agent...";

            default:
                break;
        }

        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : chatMessages) {
            if (msg.getMessageType() == MessageType.USER) {
                sb.append("User: ");
            } else {
                sb.append("Assistant: ");
            }
            sb.append(msg.getContent()).append("\n");
        }
        return openAiChatClient.call(sb.toString());
    }

    public String askOpenAiFromEntities(List<com.example.hotelchatbot.domain.ChatMessage> history) {
        List<ChatMessageDTO> dtos = history.stream().map(msg -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setRole(msg.getSender());
            dto.setContent(msg.getContent());
            if (msg.getTimestamp() != null) {
                dto.setTimestamp(msg.getTimestamp().toString());
            }
            return dto;
        }).toList();
        ChatHistoryDTO chatHistoryDTO = new ChatHistoryDTO();
        chatHistoryDTO.setMessages(dtos);
        if (history.size() > 0) {
            chatHistoryDTO.setSessionId(history.get(0).getSession().getId());
        }
        return askOpenAi(chatHistoryDTO);
    }

    public String normalizeCityWithAI(String userCity) {
        if (userCity == null) return null;
        List<String> officialCities = hotelService.getAllHotels().stream()
            .map(h -> h.getCity())
            .distinct()
            .toList();
        String cityList = String.join(", ", officialCities);
        String prompt = """
        Here are some city names: [%s].
        Which one is closest to what the user typed: '%s'?
        If none are close, say null. Only provide the city name.
        """.formatted(cityList, userCity);
        String response = openAiChatClient.call(prompt).trim();
        return response; 
    }

    private String extractHotelName(String message) {
        for (var hotel : hotelService.getAllHotels()) {
            if (message.toLowerCase().contains(hotel.getName().toLowerCase())) {
                return hotel.getName();
            }
        }
        return null;
    }

    public String fallbackOpenAi(ChatHistoryDTO chatHistoryDTO, Throwable t) {
        return "Sorry, the service is currently down";
    }
}