package com.example.backend.controller;

import com.example.backend.dto.CardResponse;
import com.example.backend.dto.InventoryItemResponse;
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
    public ResponseEntity<List<InventoryItemResponse>> getInventory(Principal principal) {
        String username = principal != null ? principal.getName() : "testuser";

        List<InventoryItemResponse> inventory = gachaService.getInventory(username)
                .stream()
                .map(InventoryItemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(inventory);
    }

    @PatchMapping("/inventory/{userCardId}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable Long userCardId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        gachaService.markAsSeen(userCardId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}