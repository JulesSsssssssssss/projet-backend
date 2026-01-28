package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.model.Card;
import com.example.backend.model.Rarity;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GachaService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    private static final int GACHA_COST = 100;

    private final SecureRandom random = new SecureRandom();

    /**
     * Pull a random card for the given user.
     */
    @Transactional
    public Card pullCard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Check if enough points
        if (user.getPoints() < GACHA_COST) {
            throw new RuntimeException("Not enough points. Required: " + GACHA_COST + ", Available: " + user.getPoints());
        }

        // Fetch all cards and group by rarity
        List<Card> allCards = cardRepository.findAll();
        List<Rarity> rarities = allCards.stream()
                .map(Card::getRarity)
                .distinct()
                .toList();

        Rarity chosenRarity = pickRarityWeighted(rarities);

        List<Card> candidates = allCards.stream()
                .filter(c -> c.getRarity().equals(chosenRarity))
                .toList();

        Card pulledCard = cardRepository.findRandomCardByRarityName("RARE");


        // Deduct points
        user.setPoints(user.getPoints() - GACHA_COST);

        // Add card to user inventory
        user.getCards().add(pulledCard);

        // Update last gacha timestamp
        user.setLastGacha(LocalDateTime.now());

        // Save
        userRepository.save(user);

        return pulledCard;
    }

    /**
     * Get or create a user with starting points.
     */
    public User getOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPoints(1000);
                    newUser.setRole("ROLE_USER");
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });
    }

    /**
     * Weighted rarity selection.
     * COMMON: 60%, RARE: 25%, EPIC: 10%, LEGENDARY: 5%
     */
    private Rarity pickRarityWeighted(List<Rarity> rarities) {
    int roll = random.nextInt(100) + 1;
    // find rarity object from list by name
    Rarity common = rarities.stream().filter(r -> r.getName().equals("COMMON")).findFirst().orElseThrow();
    Rarity rare = rarities.stream().filter(r -> r.getName().equals("RARE")).findFirst().orElseThrow();
    Rarity epic = rarities.stream().filter(r -> r.getName().equals("EPIC")).findFirst().orElseThrow();
    Rarity legendary = rarities.stream().filter(r -> r.getName().equals("LEGENDARY")).findFirst().orElseThrow();

    if (roll <= 60) return common;
    else if (roll <= 85) return rare;
    else if (roll <= 95) return epic;
    else return legendary;
}
}
