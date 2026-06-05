package com.example.backend.repository;

import com.example.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(
            value = "SELECT c.* FROM cards c JOIN rarity r ON c.rarity_id = r.id WHERE r.name = :name ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true
    )
    Card findRandomCardByRarityName(@Param("name") String name);
}
