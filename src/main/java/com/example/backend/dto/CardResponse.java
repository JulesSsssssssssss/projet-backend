package com.example.backend.dto;

public class CardResponse {

    private Long id;
    private String name;

    public CardResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
