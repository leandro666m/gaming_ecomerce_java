package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.model.Wishlist;
import com.example.gaming_ecomerce.repository.GameRepository;
import com.example.gaming_ecomerce.repository.UserRepository;
import com.example.gaming_ecomerce.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public WishlistService(WishlistRepository wishlistRepository, UserRepository userRepository, GameRepository gameRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public List<Wishlist> findAll() {
        return wishlistRepository.findAll();
    }

    public Optional<Wishlist> findById(Long id) {
        return wishlistRepository.findById(id);
    }

    public Optional<Wishlist> findByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public Wishlist save(Long userId, Wishlist wishlist) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
        wishlist.setUser(user);
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist addGame(Long wishlistId, Long gameId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist no encontrada con id: " + wishlistId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado con id: " + gameId));

        if (wishlist.getGames() == null) {
            wishlist.setGames(new ArrayList<>());
        }

        if (!wishlist.getGames().contains(game)) {
            wishlist.getGames().add(game);
        }

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist removeGame(Long wishlistId, Long gameId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist no encontrada con id: " + wishlistId));

        wishlist.getGames().removeIf(game -> game.getId() != null && game.getId().equals(gameId));
        return wishlistRepository.save(wishlist);
    }

    public void deleteById(Long id) {
        wishlistRepository.deleteById(id);
    }
}
