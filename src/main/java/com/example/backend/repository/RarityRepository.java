package com.example.backend.repository;

import com.example.backend.model.Rarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RarityRepository extends JpaRepository<Rarity, Long> {

    /**
     * Find a rarity by its name, e.g., "COMMON", "RARE"
     */
    Optional<Rarity> findByName(String name);
}
