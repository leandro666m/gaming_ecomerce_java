package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.WishlistRequest;
import com.example.gaming_ecomerce.dto.response.WishlistResponse;
import com.example.gaming_ecomerce.model.Wishlist;
import com.example.gaming_ecomerce.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/wishlists")
    public List<WishlistResponse> getAllWishlists() {
        return wishlistService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/wishlists/{id}")
    public ResponseEntity<WishlistResponse> getWishlistById(@PathVariable Long id) {
        return wishlistService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clients/{clientId}/wishlist")
    public ResponseEntity<WishlistResponse> getWishlistByClient(@PathVariable Long clientId) {
        return wishlistService.findByClientId(clientId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/clients/{clientId}/wishlist")
    public ResponseEntity<WishlistResponse> createWishlist(@PathVariable Long clientId, @Valid @RequestBody WishlistRequest request) {
        try {
            Wishlist wishlist = new Wishlist();
            if (request.getGameIds() != null && !request.getGameIds().isEmpty()) {
                wishlist.setGames(new java.util.ArrayList<>());
                request.getGameIds().forEach(gameId -> {
                    var game = new com.example.gaming_ecomerce.model.Game();
                    game.setId(gameId);
                    wishlist.getGames().add(game);
                });
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(wishlistService.save(clientId, wishlist)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/wishlists/{wishlistId}/games/{gameId}")
    public ResponseEntity<WishlistResponse> addGameToWishlist(@PathVariable Long wishlistId, @PathVariable Long gameId) {
        try {
            return ResponseEntity.ok(toResponse(wishlistService.addGame(wishlistId, gameId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/wishlists/{wishlistId}/games/{gameId}")
    public ResponseEntity<WishlistResponse> removeGameFromWishlist(@PathVariable Long wishlistId, @PathVariable Long gameId) {
        try {
            return ResponseEntity.ok(toResponse(wishlistService.removeGame(wishlistId, gameId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/wishlists/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        if (wishlistService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        wishlistService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        List<Long> gameIds = wishlist.getGames() == null ? List.of() : wishlist.getGames().stream()
                .filter(game -> game != null && game.getId() != null)
                .map(game -> game.getId())
                .toList();

        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getClient() != null ? wishlist.getClient().getId() : null,
                gameIds
        );
    }
}
