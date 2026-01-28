package com.example.backend.repository;

import com.example.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    List<Card> findByRarity(String rarity);
    
    @Query(value = "SELECT * FROM cards ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Card findRandomCard();
}
