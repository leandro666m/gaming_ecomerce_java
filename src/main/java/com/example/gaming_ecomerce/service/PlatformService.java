package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Platform;
import com.example.gaming_ecomerce.repository.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    public Optional<Platform> findById(Long id) {
        return platformRepository.findById(id);
    }

    public Optional<Platform> findBySlug(String slug) {
        return platformRepository.findBySlug(slug);
    }

    public Platform save(Platform platform) {
        return platformRepository.save(platform);
    }

    @Transactional
    public Platform update(Long id, Platform updatedPlatform) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plataforma no encontrada con id: " + id));

        platform.setName(updatedPlatform.getName());
        platform.setSlug(updatedPlatform.getSlug());
        platform.setDisplay_order(updatedPlatform.getDisplay_order());
        platform.setIconUrl(updatedPlatform.getIconUrl());

        return platformRepository.save(platform);
    }

    public void deleteById(Long id) {
        platformRepository.deleteById(id);
    }
}
