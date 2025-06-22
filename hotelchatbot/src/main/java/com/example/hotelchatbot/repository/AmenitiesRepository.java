package com.example.hotelchatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelchatbot.domain.Amenities;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, Integer>{
}
