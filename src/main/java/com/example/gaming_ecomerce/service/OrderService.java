package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Order;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.repository.OrderRepository;
import com.example.gaming_ecomerce.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<Order> findByClientIdOrderByIdDesc(Long clientId) {
        return orderRepository.findByClientIdOrderByIdDesc(clientId);
    }

    public Optional<Order> findByIdPayment(String idPayment) {
        return orderRepository.findByIdPayment(idPayment);
    }

    @Transactional
    public Order save(Long clientId, Order order) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clientId));
        order.setClient(client);
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
