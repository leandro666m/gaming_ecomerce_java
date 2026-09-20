package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.OrderItemRequest;
import com.example.gaming_ecomerce.dto.response.OrderItemResponse;
import com.example.gaming_ecomerce.model.OrderItem;
import com.example.gaming_ecomerce.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/order-items")
    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/order-items/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
        return orderItemService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getItemsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.findByOrderId(orderId).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/games/{gameId}/order-items")
    public ResponseEntity<List<OrderItemResponse>> getItemsByGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(orderItemService.findByGameId(gameId).stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping("/orders/{orderId}/games/{gameId}/items")
    public ResponseEntity<OrderItemResponse> createOrderItem(@PathVariable Long orderId, @PathVariable Long gameId, @Valid @RequestBody OrderItemRequest request) {
        try {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(request.getQuantity());
            orderItem.setUnitPrice(request.getUnitPrice());
            orderItem.setSubtotal(request.getSubtotal());
            orderItem.setDiscount(request.getDiscount());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(orderItemService.save(orderId, gameId, orderItem)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/order-items/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        if (orderItemService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        orderItemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal(),
                item.getDiscount(),
                item.getOrder() != null ? item.getOrder().getId() : null,
                item.getGame() != null ? item.getGame().getId() : null
        );
    }
}
