package com.example.backend.controller;

import com.example.backend.dto.CardTradeRequest;
import com.example.backend.model.TradeRequest;
import com.example.backend.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // Propose a trade
    @PostMapping("/propose")
    public ResponseEntity<TradeRequest> proposeTrade(
            Principal principal,
            @RequestBody @Valid CardTradeRequest request
    ) {
        String username = principal.getName();
        TradeRequest trade = tradeService.proposeTrade(username, request);
        return ResponseEntity.ok(trade);
    }

    // Get all pending trades for the authenticated user
    @GetMapping("/pending")
    public ResponseEntity<List<TradeRequest>> getPendingTrades(Principal principal) {
        String username = principal.getName();
        List<TradeRequest> pending = tradeService.getPendingTrades(username);
        return ResponseEntity.ok(pending);
    }

    // Accept a trade by ID
    @PostMapping("/accept/{tradeId}")
    public ResponseEntity<String> acceptTrade(
            Principal principal,
            @PathVariable Long tradeId
    ) {
        String username = principal.getName();
        String result = tradeService.acceptTrade(username, tradeId);
        return ResponseEntity.ok(result);
    }
}
