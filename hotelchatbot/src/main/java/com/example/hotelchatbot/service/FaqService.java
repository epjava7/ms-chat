package com.example.hotelchatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import java.util.List;

import com.example.hotelchatbot.domain.FaqEntry;
import com.example.hotelchatbot.domain.Hotel;
import com.example.hotelchatbot.repository.FaqRepository;

@Service
public class FaqService {
    
    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private OpenAiEmbeddingClient embeddingClient;

    public FaqEntry addFaq(String question, String answer, Hotel hotel) {
        List<Double> embeddingList = embeddingClient.embed(question);

        FaqEntry entry = new FaqEntry();
        entry.setQuestion(question);
        entry.setAnswer(answer);
        entry.setEmbedding(embeddingList);
        entry.setHotel(hotel);

        return faqRepository.save(entry);

        
    }

    public String findClosestAnswer(String userQuestion, Long hotelId) {
        List<Double> embeddingList = embeddingClient.embed(userQuestion);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embeddingList.size(); i++) {
            sb.append(embeddingList.get(i));
            if (i < embeddingList.size() - 1) sb.append(",");
        }
        sb.append("]");
        String embeddingStr = sb.toString();

        List<FaqEntry> neighbors = faqRepository.findNearestNeighborsForHotel(embeddingStr, hotelId);
        if (neighbors == null || neighbors.isEmpty())
            return null;
        return neighbors.get(0).getAnswer();
    }

    // public String findClosestAnswer(String userQuestion) {

    //     // https://stackoverflow.com/questions/76553746/what-jpa-hibernate-data-type-should-i-use-to-support-the-vector-extension-in-a

    //     List<Double> embeddingList = embeddingClient.embed(userQuestion);
    //     StringBuilder sb = new StringBuilder("[");
    //     for (int i = 0; i < embeddingList.size(); i++) {
    //         sb.append(embeddingList.get(i));
    //         if (i < embeddingList.size() - 1) sb.append(",");
    //     }
    //     sb.append("]");
    //     String embeddingStr = sb.toString();

    //     List<FaqEntry> neighbors;
    //     neighbors = faqRepository.findNearestNeighbors(embeddingStr);
    //     if (neighbors == null || neighbors.isEmpty())
    //         return null;
    //     return neighbors.get(0).getAnswer();
    // }
}
