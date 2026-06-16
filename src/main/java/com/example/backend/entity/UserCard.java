package com.example.backend.entity;

import com.example.backend.model.Card;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "obtained_at", nullable = false)
    private LocalDateTime obtainedAt = LocalDateTime.now();

    @Column(name = "is_new", nullable = false)
    private boolean isNew = true;

    public UserCard(User user, Card card) {
        this.user = user;
        this.card = card;
        this.obtainedAt = LocalDateTime.now();
        this.isNew = true;
    }
}