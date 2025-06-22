package com.example.hotelchatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.example.hotelchatbot.domain.HotelRoom;
import com.example.hotelchatbot.repository.HotelRoomRepository;

@Service
public class HotelRoomService {

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    public List<HotelRoom> getAllHotelRooms() {
        return hotelRoomRepository.findAll();
    }

    public Optional<HotelRoom> getHotelRoomById(int id) {
        return hotelRoomRepository.findById(id);
    }

    public HotelRoom saveHotelRoom(HotelRoom hotelRoom) {
        return hotelRoomRepository.save(hotelRoom);
    }

    
}