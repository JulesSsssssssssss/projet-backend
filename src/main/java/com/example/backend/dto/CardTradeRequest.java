package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public class CardTradeRequest {

    @NotNull
    private Long offeredCardId; // the card the current user offers

    @NotNull
    private String targetUsername; // the other player

    @NotNull
    private Long requestedCardId; // the card the current user wants from the target

    public Long getOfferedCardId() { return offeredCardId; }
    public void setOfferedCardId(Long offeredCardId) { this.offeredCardId = offeredCardId; }

    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }

    public Long getRequestedCardId() { return requestedCardId; }
    public void setRequestedCardId(Long requestedCardId) { this.requestedCardId = requestedCardId; }
}
