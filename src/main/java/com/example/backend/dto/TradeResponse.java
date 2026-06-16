package com.example.backend.dto;

import com.example.backend.model.TradeRequest;

public class TradeResponse {

    public record UserRef(String username) {}
    public record CardRef(Long id, String name) {}

    private Long id;
    private UserRef fromUser;
    private UserRef toUser;
    private CardRef offeredCard;
    private CardRef requestedCard;
    private String status;

    public static TradeResponse from(TradeRequest trade) {
        TradeResponse r = new TradeResponse();
        r.id            = trade.getId();
        r.fromUser      = new UserRef(trade.getFromUser().getUsername());
        r.toUser        = new UserRef(trade.getToUser().getUsername());
        r.offeredCard   = new CardRef(trade.getOfferedCard().getId(), trade.getOfferedCard().getName());
        r.requestedCard = new CardRef(trade.getRequestedCard().getId(), trade.getRequestedCard().getName());
        r.status        = trade.getStatus();
        return r;
    }

    public Long getId()            { return id; }
    public UserRef getFromUser()   { return fromUser; }
    public UserRef getToUser()     { return toUser; }
    public CardRef getOfferedCard()   { return offeredCard; }
    public CardRef getRequestedCard() { return requestedCard; }
    public String getStatus()      { return status; }
}
