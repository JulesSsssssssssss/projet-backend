package com.example.backend.controller;

import com.example.backend.dto.CardTradeRequest;
import com.example.backend.dto.TradeResponse;
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
    public ResponseEntity<TradeResponse> proposeTrade(
            Principal principal,
            @RequestBody @Valid CardTradeRequest request
    ) {
        String username = principal.getName();
        TradeRequest trade = tradeService.proposeTrade(username, request);
        return ResponseEntity.ok(TradeResponse.from(trade));
    }

    // Get all pending trades for the authenticated user
    @GetMapping("/pending")
    public ResponseEntity<List<TradeResponse>> getPendingTrades(Principal principal) {
        String username = principal.getName();
        List<TradeResponse> pending = tradeService.getPendingTrades(username)
                .stream().map(TradeResponse::from).toList();
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
