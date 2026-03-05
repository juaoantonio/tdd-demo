package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.application.port.PaymentGateway;
import br.dev.joaobarbosa.tdddemo.application.port.StockPort;
import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderRepository;
import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final StockPort stockPort;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        PaymentGateway paymentGateway,
                        StockPort stockPort,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.stockPort = stockPort;
        this.notificationService = notificationService;
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

    @Transactional
    public Order payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStatusException(id, order.getStatus(), "pay");
        }
        paymentGateway.charge(order.getId(), order.getTotal());
        stockPort.reserve(order.getProductSku(), order.getQuantity());
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        notificationService.notifyOrderConfirmed(order);
        return order;
    }

    @Transactional
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(id, order.getStatus(), "cancel");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            paymentGateway.refund(order.getId());
            stockPort.release(order.getProductSku(), order.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        notificationService.notifyOrderCancelled(order);
        return order;
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
