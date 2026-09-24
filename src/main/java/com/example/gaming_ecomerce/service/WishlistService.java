package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.model.Wishlist;
import com.example.gaming_ecomerce.repository.GameRepository;
import com.example.gaming_ecomerce.repository.ClientRepository;
import com.example.gaming_ecomerce.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ClientRepository clientRepository;
    private final GameRepository gameRepository;

    public WishlistService(WishlistRepository wishlistRepository, ClientRepository clientRepository, GameRepository gameRepository) {
        this.wishlistRepository = wishlistRepository;
        this.clientRepository = clientRepository;
        this.gameRepository = gameRepository;
    }

    public List<Wishlist> findAll() {
        return wishlistRepository.findAll();
    }

    public Optional<Wishlist> findById(Long id) {
        return wishlistRepository.findById(id);
    }

    public Optional<Wishlist> findByClientId(Long clientId) {
        return wishlistRepository.findByClientId(clientId);
    }

    @Transactional
    public Wishlist save(Long clientId, Wishlist wishlist) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clientId));
        wishlist.setClient(client);
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
