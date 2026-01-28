package com.example.backend.controller;

import com.example.backend.dto.GachaResponse;
import com.example.backend.model.Card;
import com.example.backend.entity.User;
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

    @PostMapping
    public ResponseEntity<GachaResponse> pullCard(Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "testuser";

            User user = gachaService.getOrCreateUser(username);
            Card pulledCard = gachaService.pullCard(username);

            GachaResponse response = new GachaResponse(
                    pulledCard,
                    user.getPoints(),
                    "You pulled a " + pulledCard.getRarity().getName() + " card!"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new GachaResponse(null, 0, e.getMessage())
            );
        }
    }

    @GetMapping("/points")
    public ResponseEntity<Integer> getUserPoints(Principal principal) {
        String username = principal != null ? principal.getName() : "testuser";
        User user = gachaService.getOrCreateUser(username);
        return ResponseEntity.ok(user.getPoints());
    }
}
