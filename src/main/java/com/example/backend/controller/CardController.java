package com.example.backend.controller;

import com.example.backend.dto.CardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/cards")
public class CardController {

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
}
