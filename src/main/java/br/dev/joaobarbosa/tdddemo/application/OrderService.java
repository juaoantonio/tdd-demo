package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderRepository;

import java.math.BigDecimal;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String customerName, BigDecimal total) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Order getOrder(Long id) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
