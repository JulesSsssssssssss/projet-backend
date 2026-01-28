package com.example.backend.repository;

import com.example.backend.model.TradeRequest;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRequestRepository extends JpaRepository<TradeRequest, Long> {

    // Fetch all pending trades for a given user
    List<TradeRequest> findByToUserAndStatus(User toUser, String status);
}
