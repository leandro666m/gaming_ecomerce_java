package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.OrderRequest;
import com.example.gaming_ecomerce.dto.response.OrderItemResponse;
import com.example.gaming_ecomerce.dto.response.OrderResponse;
import com.example.gaming_ecomerce.model.Order;
import com.example.gaming_ecomerce.model.OrderItem;
import com.example.gaming_ecomerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        return orderService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/users/{userId}/orders/recent")
    public ResponseEntity<List<OrderResponse>> getRecentOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/orders/payment/{idPayment}")
    public ResponseEntity<OrderResponse> getOrderByPaymentId(@PathVariable String idPayment) {
        return orderService.findByIdPayment(idPayment)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Long userId, @Valid @RequestBody OrderRequest request) {
        try {
            Order order = new Order();
            order.setTotalPayment(request.getTotalPayment());
            order.setIdPayment(request.getPaymentId());
            order.setShippingName(request.getShippingName());
            order.setShippingAddress(request.getShippingAddress());
            order.setShippingCity(request.getShippingCity());
            order.setShippingState(request.getShippingState());
            order.setShippingPostalCode(request.getShippingPostalCode());
            order.setShippingPhone(request.getShippingPhone());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(orderService.save(userId, order)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        try {
            Order order = new Order();
            order.setTotalPayment(request.getTotalPayment());
            order.setIdPayment(request.getPaymentId());
            order.setShippingName(request.getShippingName());
            order.setShippingAddress(request.getShippingAddress());
            order.setShippingCity(request.getShippingCity());
            order.setShippingState(request.getShippingState());
            order.setShippingPostalCode(request.getShippingPostalCode());
            order.setShippingPhone(request.getShippingPhone());
            return ResponseEntity.ok(toResponse(orderService.update(id, order)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orderService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems() == null ? List.of() : order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal(),
                        item.getDiscount(),
                        item.getOrder() != null ? item.getOrder().getId() : null,
                        item.getGame() != null ? item.getGame().getId() : null
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalPayment(),
                order.getIdPayment(),
                order.getShippingName(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingPostalCode(),
                order.getShippingPhone(),
                order.getUser() != null ? order.getUser().getId() : null,
                items
        );
    }
}
