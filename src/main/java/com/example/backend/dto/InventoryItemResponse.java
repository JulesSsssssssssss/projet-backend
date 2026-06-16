package com.example.backend.dto;

import com.example.backend.entity.UserCard;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryItemResponse {

    private Long userCardId;
    private Long cardId;
    private String name;
    private String rarity;
    private Integer attack;
    private Integer defense;
    private String imageUrl;
    private String description;
    private LocalDateTime obtainedAt;
    private boolean isNew;

    public static InventoryItemResponse from(UserCard userCard) {
        InventoryItemResponse r = new InventoryItemResponse();
        r.userCardId = userCard.getId();
        r.cardId = userCard.getCard().getId();
        r.name = userCard.getCard().getName();
        r.rarity = userCard.getCard().getRarity().getName();
        r.attack = userCard.getCard().getAttack();
        r.defense = userCard.getCard().getDefense();
        r.imageUrl = userCard.getCard().getImageUrl();
        r.description = userCard.getCard().getDescription();
        r.obtainedAt = userCard.getObtainedAt();
        r.isNew = userCard.isNew();
        return r;
    }
}