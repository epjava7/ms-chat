package com.example.hotelchatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


import com.example.hotelchatbot.domain.Hotel;
import com.example.hotelchatbot.dto.HotelDTO;
import com.example.hotelchatbot.repository.HotelRepository;
import com.example.hotelchatbot.service.ChatbotAIService;


@Service
public class HotelService {


    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(int id) {
        return hotelRepository.findById(id);
    }

    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public List<HotelDTO> searchHotels(
        String city,
        String roomType,
        Double minPrice,
        Double maxPrice,
        Integer guests,
        String propertyName,
        Integer starRating,
        String amenities
    ) {
        List<Hotel> hotels = getAllHotels();

        List<String> amenitiesList = new ArrayList<>();
        if (amenities != null && !amenities.isEmpty()) {
            for (String amenity : amenities.split(",")) {
                amenitiesList.add(amenity.trim());
            }
        }

        List<HotelDTO> dtos = new ArrayList<>();
        for (Hotel hotel : hotels) {
            if (city != null && !hotel.getCity().equalsIgnoreCase(city)) continue;
            if (propertyName != null && !propertyName.isBlank() && !hotel.getName().toLowerCase().contains(propertyName.toLowerCase())) continue;
            if (starRating != null && hotel.getStarRating() != starRating) continue;
            if (minPrice != null && hotel.getAveragePrice() < minPrice) continue;
            if (maxPrice != null && hotel.getAveragePrice() > maxPrice) continue;
            if (roomType != null && hotel.getRooms() != null) {
                boolean found = false;
                for (var room : hotel.getRooms()) {
                    if (room.getType() != null && roomType.equalsIgnoreCase(room.getType().getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;
            }
            if (guests != null && hotel.getRooms() != null) {
                boolean found = false;
                for (var room : hotel.getRooms()) {
                    if (room.getType() == null) continue;
                    String typeName = room.getType().getName().toLowerCase();
                    int capacity;
                    if (typeName.equals("single room")) capacity = 1;
                    else if (typeName.equals("double room")) capacity = 2;
                    else if (typeName.equals("luxury room") || typeName.equals("family suite")) capacity = 4;
                    else capacity = 1;
                    if (capacity >= guests) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;
            }
            if (!amenitiesList.isEmpty() && hotel.getRooms() != null) {
                boolean found = false;
                for (var room : hotel.getRooms()) {
                    if (room.getAmenities() == null) continue;
                    Set<String> roomAmenityNames = new HashSet<>();
                    for (var a : room.getAmenities()) {
                        roomAmenityNames.add(a.getName().toLowerCase());
                    }
                    boolean allMatch = true;
                    for (String amenity : amenitiesList) {
                        boolean match = false;
                        for (String roomAmenity : roomAmenityNames) {
                            if (roomAmenity.contains(amenity.toLowerCase())) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            allMatch = false;
                            break;
                        }
                    }
                    if (allMatch) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;
            }
            HotelDTO dto = new HotelDTO();
            dto.setId(hotel.getId());
            dto.setName(hotel.getName());
            dto.setCity(hotel.getCity());
            dto.setStarRating(hotel.getStarRating());
            dto.setAveragePrice(hotel.getAveragePrice());
            dto.setTimesBooked(hotel.getTimesBooked());
            dto.setImageUrl(hotel.getImageUrl());
            dto.setDescription(hotel.getDescription());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<HotelDTO> getHotels(
            String city,
            String roomType,
            Double minPrice,
            Double maxPrice,
            Integer guests,
            Integer hotelId,
            Integer starRating,
            String amenities
    ) {
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelDTO> result = new ArrayList<>();

        for (Hotel hotel : hotels) {
            if (city != null && !hotel.getCity().equalsIgnoreCase(city)) continue;
            if (starRating != null && !Objects.equals(hotel.getStarRating(), starRating)) continue;
            if (hotelId != null && !Objects.equals(hotel.getId(), hotelId)) continue;

            // You may need to add logic for roomType, minPrice, maxPrice, guests, amenities
            // For now, just filter by hotel-level criteria

            HotelDTO dto = new HotelDTO();
            dto.setId(hotel.getId());
            dto.setName(hotel.getName());
            dto.setCity(hotel.getCity());
            dto.setStarRating(hotel.getStarRating());
            dto.setDescription(hotel.getDescription());
            dto.setImageUrl(hotel.getImageUrl());
            // Add more fields as needed

            result.add(dto);
        }
        System.out.println(result);
        return result;
    }


}