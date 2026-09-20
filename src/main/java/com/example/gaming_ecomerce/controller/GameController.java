package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.GameRequest;
import com.example.gaming_ecomerce.dto.response.GameResponse;
import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.model.Platform;
import com.example.gaming_ecomerce.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/games")
    public List<GameResponse> getAllGames() {
        return gameService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
        return gameService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/games/slug/{slug}")
    public ResponseEntity<GameResponse> getGameBySlug(@PathVariable String slug) {
        return gameService.findBySlug(slug)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/platforms/{platformId}/games")
    public ResponseEntity<List<GameResponse>> getGamesByPlatform(@PathVariable Long platformId) {
        return ResponseEntity.ok(gameService.findByPlatformId(platformId).stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping("/platforms/{platformId}/games")
    public ResponseEntity<GameResponse> createGame(@PathVariable Long platformId, @Valid @RequestBody GameRequest request) {
        try {
            Game game = new Game();
            game.setTitle(request.getTitle());
            game.setSlug(request.getSlug());
            game.setPrice(request.getPrice());
            game.setSummary(request.getSummary());
            game.setVideo(request.getVideo());
            game.setCover(request.getCover());
            game.setWallpaper(request.getWallpaper());
            game.setScreenshots(request.getScreenshots());
            game.setDiscount(request.getDiscount());
            game.setReleaseDate(request.getReleaseDate());
            Platform platform = new Platform();
            platform.setId(request.getPlatformId());
            game.setPlatform(platform);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(gameService.save(platformId, game)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/games/{id}")
    public ResponseEntity<GameResponse> updateGame(@PathVariable Long id, @Valid @RequestBody GameRequest request) {
        try {
            Game game = new Game();
            game.setTitle(request.getTitle());
            game.setSlug(request.getSlug());
            game.setPrice(request.getPrice());
            game.setSummary(request.getSummary());
            game.setVideo(request.getVideo());
            game.setCover(request.getCover());
            game.setWallpaper(request.getWallpaper());
            game.setScreenshots(request.getScreenshots());
            game.setDiscount(request.getDiscount());
            game.setReleaseDate(request.getReleaseDate());
            Platform platform = new Platform();
            platform.setId(request.getPlatformId());
            game.setPlatform(platform);
            return ResponseEntity.ok(toResponse(gameService.update(id, game)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (gameService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        gameService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private GameResponse toResponse(Game game) {
        return new GameResponse(
                game.getId(),
                game.getTitle(),
                game.getSlug(),
                game.getPrice(),
                game.getSummary(),
                game.getVideo(),
                game.getCover(),
                game.getWallpaper(),
                game.getScreenshots(),
                game.getDiscount(),
                game.getReleaseDate(),
                game.getPlatform() != null ? game.getPlatform().getId() : null
        );
    }
}
