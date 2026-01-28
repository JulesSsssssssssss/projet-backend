package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World from Spring Boot!";
    }

    @GetMapping("/status")
    public String status() {
        return "Application is running!";
    }

    @GetMapping("/error")
    public String error() {
        return "404 Not Found";
    }

}
