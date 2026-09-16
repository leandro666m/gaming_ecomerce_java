package com.example.gaming_ecomerce.repository;
import com.example.gaming_ecomerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Historial de pedidos de un usuario (equivale a filtrar por user en Strapi)
    List<Order> findByUserId(Long userId);
    // Historial ordenado, el más reciente primero
    List<Order> findByUserIdOrderByIdDesc(Long userId);
    // Buscar por el identificador de pago de Stripe (idPayment del schema de Strapi)
    Optional<Order> findByIdPayment(String idPayment);
}
