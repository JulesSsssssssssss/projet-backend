package com.example.backend.dto;

import com.example.backend.model.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GachaResponse {
    private Card card;
    private Integer remainingPoints;
    private String message;
}
