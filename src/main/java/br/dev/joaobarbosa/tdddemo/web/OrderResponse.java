package br.dev.joaobarbosa.tdddemo.web;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        String productSku,
        int quantity,
        BigDecimal total,
        OrderStatus status
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getProductSku(),
                order.getQuantity(),
                order.getTotal(),
                order.getStatus()
        );
    }
}
