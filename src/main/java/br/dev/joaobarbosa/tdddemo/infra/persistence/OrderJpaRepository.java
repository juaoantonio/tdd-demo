package br.dev.joaobarbosa.tdddemo.infra.persistence;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}

