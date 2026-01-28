package com.example.backend.config;

import com.example.backend.model.Card;
import com.example.backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CardRepository cardRepository;
    
    @Override
    public void run(String... args) {
        // Initialiser des cartes si la base est vide
        if (cardRepository.count() == 0) {
            System.out.println("Initializing cards database...");
            
            // COMMON cards
            cardRepository.save(new Card(null, "Goblin", "COMMON", 50, 30, "https://via.placeholder.com/150", "Un goblin basique"));
            cardRepository.save(new Card(null, "Slime", "COMMON", 40, 40, "https://via.placeholder.com/150", "Une créature gluante"));
            cardRepository.save(new Card(null, "Rat géant", "COMMON", 45, 35, "https://via.placeholder.com/150", "Un rat de taille inhabituelle"));
            cardRepository.save(new Card(null, "Squelette", "COMMON", 55, 25, "https://via.placeholder.com/150", "Un guerrier mort-vivant"));
            
            // RARE cards
            cardRepository.save(new Card(null, "Chevalier", "RARE", 100, 80, "https://via.placeholder.com/150", "Un noble chevalier"));
            cardRepository.save(new Card(null, "Mage", "RARE", 120, 60, "https://via.placeholder.com/150", "Un lanceur de sorts"));
            cardRepository.save(new Card(null, "Archer", "RARE", 110, 70, "https://via.placeholder.com/150", "Un tireur d'élite"));
            
            // EPIC cards
            cardRepository.save(new Card(null, "Dragon Jeune", "EPIC", 200, 150, "https://via.placeholder.com/150", "Un dragon en apprentissage"));
            cardRepository.save(new Card(null, "Paladin", "EPIC", 180, 180, "https://via.placeholder.com/150", "Un guerrier sacré"));
            cardRepository.save(new Card(null, "Sorcier", "EPIC", 220, 130, "https://via.placeholder.com/150", "Un maître des arcanes"));
            
            // LEGENDARY cards
            cardRepository.save(new Card(null, "Dragon Ancien", "LEGENDARY", 400, 300, "https://via.placeholder.com/150", "Le plus puissant des dragons"));
            cardRepository.save(new Card(null, "Archmage", "LEGENDARY", 450, 250, "https://via.placeholder.com/150", "Maître suprême de la magie"));
            cardRepository.save(new Card(null, "Titan", "LEGENDARY", 500, 400, "https://via.placeholder.com/150", "Une force de la nature"));
            
            System.out.println("Database initialized with " + cardRepository.count() + " cards!");
        }
    }
}
