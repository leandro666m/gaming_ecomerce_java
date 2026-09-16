package com.example.gaming_ecomerce.repository;

import com.example.gaming_ecomerce.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    
    // Método útil para buscar una plataforma por su slug (como suele hacer el frontend de Strapi)
    Optional<Platform> findBySlug(String slug);
}
