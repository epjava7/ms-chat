package com.example.hotelchatbot.service;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntentDetectionService {

    @Autowired
    private OpenAiChatClient openAiChatClient;

    public String detectIntent(String latestMessage, List<String> history) {
        String historyText = String.join("\n", history);
        String prompt = "Given the following conversation history:\n" + historyText +
                "\nClassify the user's intent for the last message from these options: " +
                "FAQ, HOTEL_SEARCH, ROOM_AVAILABILITY, AMENITIES, BOOKING_ASSISTANCE, ROOM_SERVICE, GENERAL_INQUIRY, ESCALATE. " +
                "Respond with only the intent type. " +
                "If the user is asking to speak to a human, agent, or support, or is expressing frustration or requesting help beyond the chatbot's capabilities, classify as ESCALATE. " +
                "Last message: \"" + latestMessage + "\"";
        String response = openAiChatClient.call(prompt).trim().toUpperCase();
        return response;
    }
}