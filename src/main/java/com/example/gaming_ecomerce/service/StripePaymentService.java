package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.dto.response.PaymentIntentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StripePaymentService {

    private final String secretKey;
    private final String currency;

    public StripePaymentService(
            @Value("${STRIPE_SECRET_KEY:}") String secretKey,
            @Value("${STRIPE_CURRENCY:ars}") String currency) {
        this.secretKey = secretKey;
        this.currency = currency.toLowerCase();
    }

    public PaymentIntentResponse createIntent(
            long amount,
            Long clientId,
            String cartFingerprint) {
        configure();
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build())
                    .putMetadata("clientId", clientId.toString())
                    .putMetadata("cartFingerprint", cartFingerprint)
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            return new PaymentIntentResponse(intent.getId(), intent.getClientSecret(), intent.getAmount(), intent.getCurrency());
        } catch (StripeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe no pudo iniciar el pago.", exception);
        }
    }

    public void verifySucceeded(String intentId, long amount, Long clientId, String cartFingerprint) {
        configure();
        try {
            PaymentIntent intent = PaymentIntent.retrieve(intentId);
            boolean valid = "succeeded".equals(intent.getStatus())
                    && intent.getAmount() == amount
                    && currency.equalsIgnoreCase(intent.getCurrency())
                    && clientId.toString().equals(intent.getMetadata().get("clientId"))
                    && cartFingerprint.equals(intent.getMetadata().get("cartFingerprint"));
            if (!valid) {
                throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "El pago no está confirmado o no coincide con el carrito.");
            }
        } catch (StripeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo verificar el pago.", exception);
        }
    }

    private void configure() {
        if (secretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Los pagos aún no están configurados.");
        }
        Stripe.apiKey = secretKey;
    }

    public String currency() {
        return currency;
    }
}