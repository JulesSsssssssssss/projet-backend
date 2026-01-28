package com.example.backend.config;

import com.example.backend.entity.User;
import com.example.backend.model.Card;
import com.example.backend.model.Rarity;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.RarityRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          PasswordEncoder passwordEncoder,
                                          RarityRepository rarityRepository,
                                          CardRepository cardRepository) {
        return args -> {

            // ----- USERS -----
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                userRepository.save(admin);
            }

            User user;
            if (!userRepository.existsByUsername("user")) {
                user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole("ROLE_USER");
                user.setEnabled(true);
                userRepository.save(user);
            } else {
                user = userRepository.findByUsername("user").orElseThrow();
            }

            // ----- RARITIES -----
            // id order matters based on your SQL
            String[] rarityNames = {"COMMON", "UNUSED", "RARE", "EPIC", "LEGENDARY"};
            for (String name : rarityNames) {
                if (rarityRepository.findByName(name).isEmpty() && !name.equals("UNUSED")) {
                    rarityRepository.save(new Rarity(null, name));
                }
            }

            // Reload rarities
            Rarity common = rarityRepository.findByName("COMMON").orElseThrow();
            Rarity rare = rarityRepository.findByName("RARE").orElseThrow();
            Rarity epic = rarityRepository.findByName("EPIC").orElseThrow();
            Rarity legendary = rarityRepository.findByName("LEGENDARY").orElseThrow();

            // ----- CARDS -----
            if (cardRepository.count() == 0) {
                List<Card> cards = List.of(
                        // COMMON
                        new Card(null, "Goblin", common, 50, 30, "https://via.placeholder.com/150", "Un goblin basique"),
                        new Card(null, "Slime", common, 40, 40, "https://via.placeholder.com/150", "Une créature gluante"),
                        new Card(null, "Rat géant", common, 45, 35, "https://via.placeholder.com/150", "Un rat de taille inhabituelle"),
                        new Card(null, "Squelette", common, 55, 25, "https://via.placeholder.com/150", "Un guerrier mort-vivant"),
                        // RARE
                        new Card(null, "Chevalier", rare, 100, 80, "https://via.placeholder.com/150", "Un noble chevalier"),
                        new Card(null, "Mage", rare, 120, 60, "https://via.placeholder.com/150", "Un lanceur de sorts"),
                        new Card(null, "Archer", rare, 110, 70, "https://via.placeholder.com/150", "Un tireur d'élite"),
                        // EPIC
                        new Card(null, "Dragon Jeune", epic, 200, 150, "https://via.placeholder.com/150", "Un dragon en apprentissage"),
                        new Card(null, "Paladin", epic, 180, 180, "https://via.placeholder.com/150", "Un guerrier sacré"),
                        new Card(null, "Sorcier", epic, 220, 130, "https://via.placeholder.com/150", "Un maître des arcanes"),
                        // LEGENDARY
                        new Card(null, "Dragon Ancien", legendary, 400, 300, "https://via.placeholder.com/150", "Le plus puissant des dragons"),
                        new Card(null, "Archmage", legendary, 450, 250, "https://via.placeholder.com/150", "Maître suprême de la magie"),
                        new Card(null, "Titan", legendary, 500, 400, "https://via.placeholder.com/150", "Une force de la nature")
                );

                cardRepository.saveAll(cards);
            }

            // ----- ASSIGN SOME CARDS TO TEST USER -----
            if (user.getCards().isEmpty()) {
                List<Card> initialCards = cardRepository.findAll();
                user.getCards().add(initialCards.get(0));
                user.getCards().add(initialCards.get(1));
                userRepository.save(user);
            }
        };
    }
}
