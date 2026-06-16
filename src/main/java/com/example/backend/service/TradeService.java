package com.example.backend.service;

import com.example.backend.dto.CardTradeRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.UserCard;
import com.example.backend.model.Card;
import com.example.backend.model.TradeRequest;
import com.example.backend.repository.CardRepository;
import com.example.backend.repository.TradeRequestRepository;
import com.example.backend.repository.UserCardRepository;
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
    private final UserCardRepository userCardRepository;
    private final TradeRequestRepository tradeRequestRepository;

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

        userCardRepository.findFirstByUserIdAndCardId(fromUser.getId(), offeredCard.getId())
                .orElseThrow(() -> new RuntimeException("You do not own the offered card"));

        userCardRepository.findFirstByUserIdAndCardId(toUser.getId(), requestedCard.getId())
                .orElseThrow(() -> new RuntimeException("Target user does not own the requested card"));

        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setFromUser(fromUser);
        tradeRequest.setToUser(toUser);
        tradeRequest.setOfferedCard(offeredCard);
        tradeRequest.setRequestedCard(requestedCard);
        tradeRequest.setStatus("PENDING");

        return tradeRequestRepository.save(tradeRequest);
    }

    public List<TradeRequest> getPendingTrades(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return tradeRequestRepository.findByToUserAndStatus(user, "PENDING");
    }

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

        UserCard fromOwnership = userCardRepository.findFirstByUserIdAndCardId(fromUser.getId(), offeredCard.getId())
                .orElseThrow(() -> new RuntimeException("Sender no longer owns offered card"));
        UserCard toOwnership = userCardRepository.findFirstByUserIdAndCardId(toUser.getId(), requestedCard.getId())
                .orElseThrow(() -> new RuntimeException("You no longer own the requested card"));

        // Swap: reassign ownership
        fromOwnership.setUser(toUser);
        toOwnership.setUser(fromUser);
        userCardRepository.save(fromOwnership);
        userCardRepository.save(toOwnership);

        trade.setStatus("ACCEPTED");
        tradeRequestRepository.save(trade);

        return "Trade accepted successfully!";
    }
}
