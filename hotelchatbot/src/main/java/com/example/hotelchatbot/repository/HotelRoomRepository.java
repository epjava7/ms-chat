package com.example.hotelchatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelchatbot.domain.HotelRoom;

@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoom, Integer>{
}
