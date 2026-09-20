package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.model.Order;
import com.example.gaming_ecomerce.model.OrderItem;
import com.example.gaming_ecomerce.repository.GameRepository;
import com.example.gaming_ecomerce.repository.OrderItemRepository;
import com.example.gaming_ecomerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final GameRepository gameRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, GameRepository gameRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.gameRepository = gameRepository;
    }

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> findById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderItem> findByGameId(Long gameId) {
        return orderItemRepository.findByGameId(gameId);
    }

    @Transactional
    public OrderItem save(Long orderId, Long gameId, OrderItem orderItem) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id: " + orderId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado con id: " + gameId));

        orderItem.setOrder(order);
        orderItem.setGame(game);

        return orderItemRepository.save(orderItem);
    }

    public void deleteById(Long id) {
        orderItemRepository.deleteById(id);
    }
}
