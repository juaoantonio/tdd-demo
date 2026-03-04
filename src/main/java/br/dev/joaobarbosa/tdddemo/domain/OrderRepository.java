package br.dev.joaobarbosa.tdddemo.domain;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}

