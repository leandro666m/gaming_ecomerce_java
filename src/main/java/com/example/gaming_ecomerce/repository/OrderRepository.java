package com.example.gaming_ecomerce.repository;

import com.example.gaming_ecomerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientId(Long clientId);

    List<Order> findByClientIdOrderByIdDesc(Long clientId);

    Optional<Order> findByIdPayment(String idPayment);
}
