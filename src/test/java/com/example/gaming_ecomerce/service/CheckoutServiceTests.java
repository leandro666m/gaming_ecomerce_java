package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.dto.CheckoutItemRequest;
import com.example.gaming_ecomerce.model.Game;
import com.example.gaming_ecomerce.repository.AddressRepository;
import com.example.gaming_ecomerce.repository.GameRepository;
import com.example.gaming_ecomerce.repository.OrderItemRepository;
import com.example.gaming_ecomerce.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckoutServiceTests {

    private final GameRepository gameRepository = mock(GameRepository.class);
    private final CheckoutService checkoutService = new CheckoutService(
            gameRepository,
            mock(AddressRepository.class),
            mock(OrderRepository.class),
            mock(OrderItemRepository.class));

    @Test
    void calculatesDiscountedTotalFromDatabasePrices() {
        Game game = new Game();
        game.setPrice(new BigDecimal("59.99"));
        game.setDiscount(10);
        when(gameRepository.findById(4L)).thenReturn(Optional.of(game));

        var cart = checkoutService.price(List.of(new CheckoutItemRequest(4L, 2)));

        assertEquals(new BigDecimal("107.98"), cart.total());
        assertEquals(10798L, checkoutService.toMinorUnits(cart.total()));
    }

    @Test
    void rejectsDuplicateGamesInCheckoutRequest() {
        assertThrows(IllegalArgumentException.class, () -> checkoutService.price(List.of(
                new CheckoutItemRequest(4L, 1),
                new CheckoutItemRequest(4L, 2))));
    }

    @Test
    void rejectsDiscountsOutsideTheValidRange() {
        Game game = new Game();
        game.setPrice(new BigDecimal("10.00"));
        game.setDiscount(101);
        when(gameRepository.findById(5L)).thenReturn(Optional.of(game));

        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.price(List.of(new CheckoutItemRequest(5L, 1))));
    }

    @Test
    void cartFingerprintDoesNotDependOnInputOrder() {
        List<CheckoutItemRequest> first = List.of(
                new CheckoutItemRequest(4L, 1),
                new CheckoutItemRequest(9L, 2));
        List<CheckoutItemRequest> second = List.of(
                new CheckoutItemRequest(9L, 2),
                new CheckoutItemRequest(4L, 1));

        assertEquals(checkoutService.fingerprint(first), checkoutService.fingerprint(second));
    }
}