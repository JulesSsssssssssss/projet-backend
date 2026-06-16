package com.example.backend.repository;

import com.example.backend.entity.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    List<UserCard> findByUserId(Long userId);

    Optional<UserCard> findByIdAndUserId(Long id, Long userId);

    Optional<UserCard> findFirstByUserIdAndCardId(Long userId, Long cardId);
}