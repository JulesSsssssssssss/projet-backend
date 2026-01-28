package com.example.backend.service;

import com.example.backend.model.Card;
import com.example.backend.model.Player;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GachaService {
    
    private final CardRepository cardRepository;
    private final PlayerRepository playerRepository;
    
    private static final int GACHA_COST = 100; // Coût en points pour 1 gacha
    
    @Transactional
    public Card pullCard(String username) {
        // Récupérer le joueur
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Player not found: " + username));
        
        // Vérifier si le joueur a assez de points
        if (player.getPoints() < GACHA_COST) {
            throw new RuntimeException("Not enough points. Required: " + GACHA_COST + ", Available: " + player.getPoints());
        }
        
        // Tirer une carte aléatoire
        Card randomCard = cardRepository.findRandomCard();
        if (randomCard == null) {
            throw new RuntimeException("No cards available in the database");
        }
        
        // Déduire les points
        player.setPoints(player.getPoints() - GACHA_COST);
        
        // Ajouter la carte à l'inventaire du joueur
        player.getCards().add(randomCard);
        
        // Sauvegarder
        playerRepository.save(player);
        
        return randomCard;
    }
    
    public Player getOrCreatePlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setUsername(username);
                    newPlayer.setPoints(1000); // Points de départ
                    return playerRepository.save(newPlayer);
                });
    }
}
