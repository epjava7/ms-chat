package com.example.hotelchatbot.controller;

import com.example.hotelchatbot.domain.Hotel;
import com.example.hotelchatbot.dto.HotelDTO;
import com.example.hotelchatbot.dto.HotelDetailsDTO;
import com.example.hotelchatbot.dto.HotelRoomDTO;
import com.example.hotelchatbot.service.ChatbotAIService;
import com.example.hotelchatbot.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "http://localhost:3001")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private OpenAiChatClient openAiChatClient;

    @Autowired
    private ChatbotAIService chatbotAIService;

    @PostMapping("/search-ai")
    public List<HotelDTO> searchHotelsAI(@RequestBody String userQuery) {
        String prompt = """
            Extract these from the user request: 
            - city
            - starRating (1-5)
            - minPrice
            - maxPrice
            - amenities (comma separated)
            - roomType
            - guests
            If a field is not mentioned, return null.
            Respond in JSON format. 
            User request: "%s"
            """.formatted(userQuery);

        String aiResponse = openAiChatClient.call(prompt);

        System.out.println(aiResponse);

        Map<String, Object> criteria = null;
        try {
            criteria = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(aiResponse, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String city = chatbotAIService.normalizeCityWithAI((String) criteria.get("city"));
        String roomType = (String) criteria.get("roomType");
        Double minPrice = criteria.get("minPrice") != null ? Double.valueOf(criteria.get("minPrice").toString()) : null;
        Double maxPrice = criteria.get("maxPrice") != null ? Double.valueOf(criteria.get("maxPrice").toString()) : null;
        Integer guests = criteria.get("guests") != null ? Integer.valueOf(criteria.get("guests").toString()) : null;
        String amenities = (String) criteria.get("amenities");
        Integer starRating = criteria.get("starRating") != null ? Integer.valueOf(criteria.get("starRating").toString()) : null;

        return getHotels(city, roomType, minPrice, maxPrice, guests, null, starRating, amenities);
    }

    @GetMapping("/{id}")
    public HotelDetailsDTO getHotelById(@PathVariable int id) {
        Hotel hotel = hotelService.getHotelById(id)
            .orElseThrow();

        HotelDetailsDTO dto = new HotelDetailsDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setImageUrl(hotel.getImageUrl());
        dto.setStarRating(hotel.getStarRating());
        dto.setAddress(hotel.getAddress());
        dto.setCity(hotel.getCity());

        List<String> amenities = new java.util.ArrayList<>();
        if (hotel.getAmenities() != null) {
            for (var a : hotel.getAmenities()) {
                amenities.add(a.getName());
            }
        }
        dto.setAmenities(amenities);


        List<HotelRoomDTO> rooms = new java.util.ArrayList<>();
        if (hotel.getRooms() != null) {
            for (var room : hotel.getRooms()) {
                HotelRoomDTO roomDto = new HotelRoomDTO();
                roomDto.setId(room.getId());
                if (room.getType() != null) {
                    roomDto.setType(room.getType().getName());
                } else {
                    roomDto.setType(null);
                }
                roomDto.setPrice(room.getPrice());
                roomDto.setDescription(room.getDescription());

                List<String> roomAmenities = new java.util.ArrayList<>();
                if (room.getAmenities() != null) {
                    for (var a : room.getAmenities()) {
                        roomAmenities.add(a.getName());
                    }
                }
                roomDto.setAmenities(roomAmenities);

                rooms.add(roomDto);
            }
        }
        dto.setRooms(rooms);

        return dto;
    }

    @GetMapping
    public List<HotelDTO> getHotels(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String roomType,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Integer guests,
        @RequestParam(required = false) String propertyName,
        @RequestParam(required = false) Integer starRating,
        @RequestParam(required = false) String amenities 
    ) {
        return hotelService.searchHotels(city, roomType, minPrice, maxPrice, guests, propertyName, starRating, amenities);
    }

}