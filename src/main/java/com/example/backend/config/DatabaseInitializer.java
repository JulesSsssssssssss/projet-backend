package com.example.backend.config;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseInitializer {
    
    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Vérifier si les utilisateurs existent déjà
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("Utilisateur admin créé");
            }
            
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole("ROLE_USER");
                user.setEnabled(true);
                userRepository.save(user);
                System.out.println("Utilisateur user créé");
            }
        };
    }
}
