package com.example.hotelchatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.example.hotelchatbot.domain.Amenities;
import com.example.hotelchatbot.repository.AmenitiesRepository;

@Service
public class AmenitiesService {

    @Autowired
    private AmenitiesRepository amenitiesRepository;

    public List<Amenities> getAllAmenities() {
        return amenitiesRepository.findAll();
    }

    public Optional<Amenities> getAmenityById(int id) {
        return amenitiesRepository.findById(id);
    }

    public Amenities saveAmenity(Amenities amenities) {
        return amenitiesRepository.save(amenities);
    }

}
