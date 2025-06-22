package com.example.hotelchatbot.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import com.example.hotelchatbot.domain.FaqEntry;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntry, Long>{

    // https://stackoverflow.com/questions/76553746/what-jpa-hibernate-data-type-should-i-use-to-support-the-vector-extension-in-a
    
    // @Query(nativeQuery = true, 
    //     value = "SELECT * FROM faq_entries ORDER BY embedding <-> cast(?1 as vector) LIMIT 3")
    // List<FaqEntry> findNearestNeighbors(String embedding);

    @Query(nativeQuery = true, 
        value = "SELECT * FROM faq_entries WHERE hotel_id = ?2 ORDER BY embedding <-> cast(?1 as vector) LIMIT 3")
    List<FaqEntry> findNearestNeighborsForHotel(String embedding, Long hotelId);
}
