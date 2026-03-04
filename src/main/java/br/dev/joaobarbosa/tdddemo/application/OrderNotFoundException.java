package br.dev.joaobarbosa.tdddemo.application;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}

