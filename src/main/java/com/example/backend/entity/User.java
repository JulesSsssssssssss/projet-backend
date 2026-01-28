package com.example.backend.entity;

import com.example.backend.model.Card;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private boolean enabled = true;

    /**
     * Last time the user pulled a gacha.
     * Used for cooldowns, daily pulls, or analytics.
     */
    private LocalDateTime lastGacha;

    /**
     * Cards owned by the user.
     * Many-to-Many relationship via 'user_cards' pivot table.
     */
    @ManyToMany
    @JoinTable(
        name = "user_cards",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cards = new HashSet<>();

    /**
     * Points for gacha pulls (can later be persisted if needed)
     */
    @Transient
    private Integer points = 1000;

    /**
     * Convenience constructor for new users
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }
}
