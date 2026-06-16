package com.example.backend.controller;

import com.example.backend.dto.GachaResponse;
import com.example.backend.model.Card;
import com.example.backend.service.GachaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = principal.getName();

            GachaService.PullResult result = gachaService.pullCard(username);
            Card pulledCard = result.card();

            GachaResponse response = new GachaResponse(
                    pulledCard,
                    result.remainingPoints(),
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
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        return ResponseEntity.ok(gachaService.getUserPoints(username));
    }
}
