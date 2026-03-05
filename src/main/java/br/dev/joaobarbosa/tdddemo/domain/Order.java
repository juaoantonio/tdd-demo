package br.dev.joaobarbosa.tdddemo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String productSku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    public Order(String customerName, String customerEmail, String productSku, int quantity, BigDecimal total) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.productSku = productSku;
        this.quantity = quantity;
        this.total = total;
        this.status = OrderStatus.CREATED;
    }
}
