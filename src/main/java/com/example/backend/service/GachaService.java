package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.entity.UserCard;
import com.example.backend.model.Card;
import com.example.backend.model.Rarity;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.UserCardRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GachaService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;

    private static final int GACHA_COST = 100;

    private final SecureRandom random = new SecureRandom();

    public record PullResult(Card card, int remainingPoints) {}

    @Transactional
    public PullResult pullCard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getPoints() == null) {
            user.setPoints(1000);
        }

        if (user.getPoints() < GACHA_COST) {
            throw new RuntimeException("Not enough points. Required: " + GACHA_COST + ", Available: " + user.getPoints());
        }

        List<Card> allCards = cardRepository.findAll();
        List<Rarity> rarities = allCards.stream()
                .map(Card::getRarity)
                .distinct()
                .toList();

        Rarity chosenRarity = pickRarityWeighted(rarities);

        Card pulledCard = cardRepository.findRandomCardByRarityName(chosenRarity.getName());

        user.setPoints(user.getPoints() - GACHA_COST);
        user.setLastGacha(LocalDateTime.now());

        UserCard userCard = new UserCard(user, pulledCard);
        userCardRepository.save(userCard);

        userRepository.save(user);

        return new PullResult(pulledCard, user.getPoints());
    }

    @Transactional
    public int getUserPoints(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (user.getPoints() == null) {
            user.setPoints(1000);
            userRepository.save(user);
        }
        return user.getPoints();
    }

    public List<UserCard> getInventory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return userCardRepository.findByUserId(user.getId());
    }

    @Transactional
    public void markAsSeen(Long userCardId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        UserCard userCard = userCardRepository.findByIdAndUserId(userCardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Card not found in inventory"));
        userCard.setNew(false);
        userCardRepository.save(userCard);
    }

    private Rarity pickRarityWeighted(List<Rarity> rarities) {
        int roll = random.nextInt(100) + 1;
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