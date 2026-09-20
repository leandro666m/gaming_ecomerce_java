package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.model.Platform;
import com.example.gaming_ecomerce.repository.GameRepository;
import com.example.gaming_ecomerce.repository.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;

    public GameService(GameRepository gameRepository, PlatformRepository platformRepository) {
        this.gameRepository = gameRepository;
        this.platformRepository = platformRepository;
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    public Optional<Game> findBySlug(String slug) {
        return gameRepository.findBySlug(slug);
    }

    public List<Game> findByPlatformId(Long platformId) {
        return gameRepository.findByPlatformId(platformId);
    }

    @Transactional
    public Game save(Long platformId, Game game) {
        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new IllegalArgumentException("Plataforma no encontrada con id: " + platformId));
        game.setPlatform(platform);
        return gameRepository.save(game);
    }

    @Transactional
    public Game update(Long id, Game updatedGame) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado con id: " + id));

        game.setTitle(updatedGame.getTitle());
        game.setSlug(updatedGame.getSlug());
        game.setPrice(updatedGame.getPrice());
        game.setSummary(updatedGame.getSummary());
        game.setVideo(updatedGame.getVideo());
        game.setCover(updatedGame.getCover());
        game.setWallpaper(updatedGame.getWallpaper());
        game.setScreenshots(updatedGame.getScreenshots());
        game.setDiscount(updatedGame.getDiscount());
        game.setReleaseDate(updatedGame.getReleaseDate());

        if (updatedGame.getPlatform() != null && updatedGame.getPlatform().getId() != null) {
            Platform platform = platformRepository.findById(updatedGame.getPlatform().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Plataforma no encontrada con id: " + updatedGame.getPlatform().getId()));
            game.setPlatform(platform);
        }

        return gameRepository.save(game);
    }

    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }
}
