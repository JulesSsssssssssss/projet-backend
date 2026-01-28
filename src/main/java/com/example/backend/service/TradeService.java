package com.example.backend.service;

import com.example.backend.dto.CardTradeRequest;
import com.example.backend.entity.User;
import com.example.backend.model.Card;
import com.example.backend.model.TradeRequest;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.TradeRequestRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TradeRequestRepository tradeRequestRepository;

    /**
     * Propose a trade request
     */
    @Transactional
    public TradeRequest proposeTrade(String username, CardTradeRequest request) {
        User fromUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        User toUser = userRepository.findByUsername(request.getTargetUsername())
                .orElseThrow(() -> new RuntimeException("Target user not found: " + request.getTargetUsername()));

        Card offeredCard = cardRepository.findById(request.getOfferedCardId())
                .orElseThrow(() -> new RuntimeException("Offered card not found: " + request.getOfferedCardId()));

        Card requestedCard = cardRepository.findById(request.getRequestedCardId())
                .orElseThrow(() -> new RuntimeException("Requested card not found: " + request.getRequestedCardId()));

        if (!fromUser.getCards().contains(offeredCard)) {
            throw new RuntimeException("You do not own the offered card");
        }

        if (!toUser.getCards().contains(requestedCard)) {
            throw new RuntimeException("Target user does not own the requested card");
        }

        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setFromUser(fromUser);
        tradeRequest.setToUser(toUser);
        tradeRequest.setOfferedCard(offeredCard);
        tradeRequest.setRequestedCard(requestedCard);
        tradeRequest.setStatus("PENDING");

        return tradeRequestRepository.save(tradeRequest);
    }

    /**
     * List all pending trade requests for a user
     */
    public List<TradeRequest> getPendingTrades(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return tradeRequestRepository.findByToUserAndStatus(user, "PENDING");
    }

    /**
     * Accept a pending trade request
     */
    @Transactional
    public String acceptTrade(String username, Long tradeId) {
        TradeRequest trade = tradeRequestRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade request not found: " + tradeId));

        if (!trade.getToUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to accept this trade");
        }

        if (!trade.getStatus().equals("PENDING")) {
            throw new RuntimeException("Trade is not pending");
        }

        User fromUser = trade.getFromUser();
        User toUser = trade.getToUser();
        Card offeredCard = trade.getOfferedCard();
        Card requestedCard = trade.getRequestedCard();

        // Validate ownership again
        if (!fromUser.getCards().contains(offeredCard)) {
            throw new RuntimeException("Sender no longer owns offered card");
        }
        if (!toUser.getCards().contains(requestedCard)) {
            throw new RuntimeException("You no longer own requested card");
        }

        // Swap the cards
        fromUser.getCards().remove(offeredCard);
        toUser.getCards().remove(requestedCard);

        fromUser.getCards().add(requestedCard);
        toUser.getCards().add(offeredCard);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        // Mark trade as accepted
        trade.setStatus("ACCEPTED");
        tradeRequestRepository.save(trade);

        return "Trade accepted successfully!";
    }
}
