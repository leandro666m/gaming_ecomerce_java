package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.CompleteCheckoutRequest;
import com.example.gaming_ecomerce.dto.CreatePaymentIntentRequest;
import com.example.gaming_ecomerce.dto.response.PaymentIntentResponse;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.service.CheckoutService;
import com.example.gaming_ecomerce.service.ClientOwnershipService;
import com.example.gaming_ecomerce.service.StripePaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final StripePaymentService stripePaymentService;
    private final ClientOwnershipService clientOwnershipService;

    public CheckoutController(
            CheckoutService checkoutService,
            StripePaymentService stripePaymentService,
            ClientOwnershipService clientOwnershipService) {
        this.checkoutService = checkoutService;
        this.stripePaymentService = stripePaymentService;
        this.clientOwnershipService = clientOwnershipService;
    }

    @PostMapping("/payment-intent")
    public PaymentIntentResponse createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request,
            Authentication authentication) {
        Client client = clientOwnershipService.currentClient(authentication);
        var cart = checkoutService.price(request.items());
        return stripePaymentService.createIntent(
                checkoutService.toMinorUnits(cart.total()),
                client.getId(),
                checkoutService.fingerprint(request.items()));
    }

    @PostMapping("/complete")
    public ResponseEntity<CheckoutCompletionResponse> completeCheckout(
            @Valid @RequestBody CompleteCheckoutRequest request,
            Authentication authentication) {
        Client client = clientOwnershipService.currentClient(authentication);
        clientOwnershipService.requireAddress(authentication, request.addressId());
        var cart = checkoutService.price(request.items());
        stripePaymentService.verifySucceeded(
                request.paymentIntentId(),
                checkoutService.toMinorUnits(cart.total()),
                client.getId(),
                checkoutService.fingerprint(request.items()));
        Long orderId = checkoutService.createOrder(client, request.addressId(), request.paymentIntentId(), cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CheckoutCompletionResponse(orderId));
    }

    public record CheckoutCompletionResponse(Long orderId) { }
}