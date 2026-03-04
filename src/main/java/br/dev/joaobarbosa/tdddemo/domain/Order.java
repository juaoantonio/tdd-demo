package br.dev.joaobarbosa.tdddemo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private Long id;
    private String customerName;
    private BigDecimal total;
    private OrderStatus status;

    public Order(String customerName, BigDecimal total) {
        this.customerName = customerName;
        this.total = total;
        this.status = OrderStatus.CREATED;
    }
}
