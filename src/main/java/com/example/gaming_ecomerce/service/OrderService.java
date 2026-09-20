package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Order;
import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.repository.OrderRepository;
import com.example.gaming_ecomerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findByUserIdOrderByIdDesc(Long userId) {
        return orderRepository.findByUserIdOrderByIdDesc(userId);
    }

    public Optional<Order> findByIdPayment(String idPayment) {
        return orderRepository.findByIdPayment(idPayment);
    }

    @Transactional
    public Order save(Long userId, Order order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
        order.setUser(user);
        return orderRepository.save(order);
    }

    @Transactional
    public Order update(Long id, Order updatedOrder) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id: " + id));

        order.setTotalPayment(updatedOrder.getTotalPayment());
        order.setIdPayment(updatedOrder.getIdPayment());
        order.setShippingName(updatedOrder.getShippingName());
        order.setShippingAddress(updatedOrder.getShippingAddress());
        order.setShippingCity(updatedOrder.getShippingCity());
        order.setShippingState(updatedOrder.getShippingState());
        order.setShippingPostalCode(updatedOrder.getShippingPostalCode());
        order.setShippingPhone(updatedOrder.getShippingPhone());

        return orderRepository.save(order);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
