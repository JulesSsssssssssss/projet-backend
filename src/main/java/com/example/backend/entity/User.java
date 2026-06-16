package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String role;

    private boolean enabled = true;

    /**
     * Last time the user pulled a gacha.
     * Used for cooldowns, daily pulls, or analytics.
     */
    private LocalDateTime lastGacha;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCard> userCards = new ArrayList<>();

    /**
        * Points for gacha pulls.
     */
        @Column(nullable = false)
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
