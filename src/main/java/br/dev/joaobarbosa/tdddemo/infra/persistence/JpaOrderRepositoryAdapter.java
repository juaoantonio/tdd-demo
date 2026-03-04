package br.dev.joaobarbosa.tdddemo.infra.persistence;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public JpaOrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id);
    }
}

