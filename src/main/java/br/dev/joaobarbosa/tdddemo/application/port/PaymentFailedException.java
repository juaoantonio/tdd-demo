package br.dev.joaobarbosa.tdddemo.application.port;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(Long orderId, String reason) {
        super("Payment failed for order " + orderId + ": " + reason);
    }
}
