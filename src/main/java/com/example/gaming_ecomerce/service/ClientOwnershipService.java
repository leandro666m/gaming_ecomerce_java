package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.repository.AddressRepository;
import com.example.gaming_ecomerce.repository.ClientRepository;
import com.example.gaming_ecomerce.repository.OrderRepository;
import com.example.gaming_ecomerce.repository.WishlistRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ClientOwnershipService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final WishlistRepository wishlistRepository;
    private final OrderRepository orderRepository;

    public ClientOwnershipService(
            ClientRepository clientRepository,
            AddressRepository addressRepository,
            WishlistRepository wishlistRepository,
            OrderRepository orderRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.wishlistRepository = wishlistRepository;
        this.orderRepository = orderRepository;
    }

    public void requireClient(Authentication authentication, Long clientId) {
        Client client = currentClient(authentication);
        if (!client.getId().equals(clientId)) {
            throw new AccessDeniedException("No se puede acceder a los recursos de otro cliente.");
        }
    }

    public void requireAddress(Authentication authentication, Long addressId) {
        Long clientId = currentClient(authentication).getId();
        boolean owned = addressRepository.findById(addressId)
                .map(address -> address.getClient().getId().equals(clientId))
                .orElse(false);
        if (!owned) throw new AccessDeniedException("Dirección no disponible.");
    }

    public void requireWishlist(Authentication authentication, Long wishlistId) {
        Long clientId = currentClient(authentication).getId();
        boolean owned = wishlistRepository.findById(wishlistId)
                .map(wishlist -> wishlist.getClient().getId().equals(clientId))
                .orElse(false);
        if (!owned) throw new AccessDeniedException("Lista de deseos no disponible.");
    }

    public void requireOrder(Authentication authentication, Long orderId) {
        Long clientId = currentClient(authentication).getId();
        boolean owned = orderRepository.findById(orderId)
                .map(order -> order.getClient().getId().equals(clientId))
                .orElse(false);
        if (!owned) throw new AccessDeniedException("Pedido no disponible.");
    }

    public Client currentClient(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Se requiere una sesión de cliente.");
        }
        return clientRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Se requiere una sesión de cliente."));
    }
}
