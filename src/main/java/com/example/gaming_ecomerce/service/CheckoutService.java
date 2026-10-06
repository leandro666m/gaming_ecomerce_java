package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.dto.CheckoutItemRequest;
import com.example.gaming_ecomerce.model.Address;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.model.Order;
import com.example.gaming_ecomerce.model.OrderItem;
import com.example.gaming_ecomerce.repository.AddressRepository;
import com.example.gaming_ecomerce.repository.GameRepository;
import com.example.gaming_ecomerce.repository.OrderItemRepository;
import com.example.gaming_ecomerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CheckoutService {

    private final GameRepository gameRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CheckoutService(
            GameRepository gameRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.gameRepository = gameRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public PricedCart price(List<CheckoutItemRequest> items) {
        Set<Long> seen = new HashSet<>();
        List<PricedLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (CheckoutItemRequest item : items) {
            if (!seen.add(item.gameId())) {
                throw new IllegalArgumentException("El carrito contiene juegos duplicados.");
            }
            Game game = gameRepository.findById(item.gameId())
                    .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + item.gameId()));
                int discount = game.getDiscount() == null ? 0 : game.getDiscount();
                if (game.getPrice() == null || game.getPrice().signum() < 0 || discount < 0 || discount > 100) {
                throw new IllegalArgumentException("El precio o el descuento del juego no son válidos.");
                }
                BigDecimal discountFactor = BigDecimal.valueOf(100L - discount)
                    .movePointLeft(2);
            BigDecimal unitPrice = game.getPrice().multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.quantity())).setScale(2, RoundingMode.HALF_UP);
            lines.add(new PricedLine(game, item.quantity(), unitPrice, subtotal));
            total = total.add(subtotal);
        }

        return new PricedCart(List.copyOf(lines), total.setScale(2, RoundingMode.HALF_UP));
    }

    public long toMinorUnits(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    public String fingerprint(List<CheckoutItemRequest> items) {
        String canonical = items.stream()
                .sorted(Comparator.comparing(CheckoutItemRequest::gameId))
                .map(item -> item.gameId() + ":" + item.quantity())
                .reduce((first, second) -> first + ";" + second)
                .orElse("");
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(canonical.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no disponible.", exception);
        }
    }

    @Transactional
    public Long createOrder(Client client, Long addressId, String paymentIntentId, PricedCart cart) {
        if (orderRepository.existsByIdPayment(paymentIntentId)) {
            return orderRepository.findByIdPayment(paymentIntentId)
                    .orElseThrow().getId();
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada."));
        if (!address.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("La dirección no pertenece al cliente.");
        }

        Order order = new Order();
        order.setClient(client);
        order.setTotalPayment(cart.total());
        order.setIdPayment(paymentIntentId);
        order.setShippingName(address.getName());
        order.setShippingAddress(address.getAddress());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingPhone(address.getPhone());
        orderRepository.save(order);

        List<OrderItem> orderItems = cart.lines().stream().map(line -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setGame(line.game());
            item.setQuantity(line.quantity());
            item.setUnitPrice(line.game().getPrice());
            item.setSubtotal(line.subtotal());
            item.setDiscount(line.game().getDiscount());
            return item;
        }).toList();
        orderItemRepository.saveAll(orderItems);
        return order.getId();
    }

    public record PricedLine(Game game, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) { }

    public record PricedCart(List<PricedLine> lines, BigDecimal total) { }
}