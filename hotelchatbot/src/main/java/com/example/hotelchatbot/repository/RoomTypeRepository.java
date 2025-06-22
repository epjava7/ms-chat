package com.example.hotelchatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelchatbot.domain.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer>{
}
