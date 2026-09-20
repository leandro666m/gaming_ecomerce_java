package com.example.gaming_ecomerce.repository;

import com.example.gaming_ecomerce.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findBySlug(String slug);

    List<Game> findByPlatformId(Long platformId);
}
