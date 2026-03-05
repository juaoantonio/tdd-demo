package br.dev.joaobarbosa.tdddemo.web;

import br.dev.joaobarbosa.tdddemo.application.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var order = orderService.createOrder(
                request.customerName(),
                request.customerEmail(),
                request.productSku(),
                request.quantity(),
                request.total());
        return OrderResponse.from(order);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        var order = orderService.getOrder(id);
        return OrderResponse.from(order);
    }

    @PostMapping("/{id}/pay")
    public OrderResponse payOrder(@PathVariable Long id) {
        var order = orderService.payOrder(id);
        return OrderResponse.from(order);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        var order = orderService.cancelOrder(id);
        return OrderResponse.from(order);
    }
}

