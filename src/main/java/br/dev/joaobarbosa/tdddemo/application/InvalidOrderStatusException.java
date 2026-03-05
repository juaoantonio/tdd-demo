package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException(Long orderId, OrderStatus current, String action) {
        super("Cannot " + action + " order " + orderId + " with status " + current);
    }
}
