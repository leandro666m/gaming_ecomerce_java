package com.example.gaming_ecomerce.repository;

import com.example.gaming_ecomerce.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    // Buscar un juego por su slug
    Optional<Game> findBySlug(String slug);
    
    // Buscar juegos que pertenezcan a una plataforma específica (por ID de plataforma)
    List<Game> findByPlatformId(Long platformId);
}
