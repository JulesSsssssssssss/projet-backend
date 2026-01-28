package com.example.backend.model;

import com.example.backend.entity.User;
import com.example.backend.model.Card;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trade_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who initiates the trade
    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    // The target user
    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    // Cards involved
    @ManyToOne
    @JoinColumn(name = "offered_card_id", nullable = false)
    private Card offeredCard;

    @ManyToOne
    @JoinColumn(name = "requested_card_id", nullable = false)
    private Card requestedCard;

    // Status: PENDING, ACCEPTED, REJECTED
    @Column(nullable = false)
    private String status = "PENDING";
}
