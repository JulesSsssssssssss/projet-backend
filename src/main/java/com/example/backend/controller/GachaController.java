package com.example.backend.controller;

import com.example.backend.dto.GachaResponse;
import com.example.backend.model.Card;
import com.example.backend.model.Player;
import com.example.backend.service.GachaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/gacha")
@RequiredArgsConstructor
public class GachaController {
    
    private final GachaService gachaService;
    
    /**
     * POST /api/gacha
     * Tire une carte aléatoire pour le joueur authentifié
     */
    @PostMapping
    public ResponseEntity<GachaResponse> pullCard(Principal principal) {
        try {
            // Récupérer le username depuis le token JWT
            String username = principal != null ? principal.getName() : "testuser";
            
            // Assurer que le joueur existe
            Player player = gachaService.getOrCreatePlayer(username);
            
            // Tirer une carte
            Card pulledCard = gachaService.pullCard(username);
            
            // Récupérer les points restants
            player = gachaService.getOrCreatePlayer(username);
            
            GachaResponse response = new GachaResponse(
                pulledCard,
                player.getPoints(),
                "You pulled a " + pulledCard.getRarity() + " card!"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                new GachaResponse(null, 0, e.getMessage())
            );
        }
    }
    
    /**
     * GET /api/gacha/points
     * Récupère les points du joueur
     */
    @GetMapping("/points")
    public ResponseEntity<Integer> getPlayerPoints(Principal principal) {
        String username = principal != null ? principal.getName() : "testuser";
        Player player = gachaService.getOrCreatePlayer(username);
        return ResponseEntity.ok(player.getPoints());
    }
}
