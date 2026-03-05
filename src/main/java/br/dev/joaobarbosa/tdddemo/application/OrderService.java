package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(String customerName, String customerEmail, String productSku, int quantity, BigDecimal total) {
        validateCustomerName(customerName);
        validateEmail(customerEmail);
        validateProductSku(productSku);
        validateQuantity(quantity);
        validateTotal(total);
        Order order = new Order(customerName, customerEmail, productSku, quantity, total);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private void validateCustomerName(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be empty");
        }
    }

    private void validateEmail(String customerEmail) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer email must not be empty");
        }
    }

    private void validateProductSku(String productSku) {
        if (productSku == null || productSku.isBlank()) {
            throw new IllegalArgumentException("Product SKU must not be empty");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
    }

    private void validateTotal(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total must be greater than zero");
        }
    }
}
