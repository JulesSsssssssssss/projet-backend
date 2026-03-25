package com.example.backend.controller;

import com.example.backend.dto.CardResponse;
import com.example.backend.model.Card;
import com.example.backend.service.GachaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final GachaService gachaService;

    // Temporary mock data (shared)
    private final List<CardResponse> cards = List.of(
            new CardResponse(1L, "Fire Dragon"),
            new CardResponse(2L, "Water Mage"),
            new CardResponse(3L, "Shadow Assassin")
    );

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<CardResponse>> getInventory(Principal principal) {
        String username = principal != null ? principal.getName() : "testuser";

        List<CardResponse> inventory = gachaService.getInventory(username)
                .stream()
                .map(this::toCardResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(inventory);
    }

    private CardResponse toCardResponse(Card card) {
        return new CardResponse(card.getId(), card.getName());
    }
}
