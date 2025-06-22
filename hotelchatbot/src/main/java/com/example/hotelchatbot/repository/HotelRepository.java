package com.example.hotelchatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelchatbot.domain.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer>{
}
