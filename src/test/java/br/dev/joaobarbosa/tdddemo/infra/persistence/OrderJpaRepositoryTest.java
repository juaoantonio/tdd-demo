package br.dev.joaobarbosa.tdddemo.infra.persistence;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderJpaRepositoryTest {

    @Autowired
    private OrderJpaRepository repository;

    @Test
    @DisplayName("deve salvar e buscar pedido por id")
    void shouldSaveAndFindOrderById() {
        Order order = new Order("Carlos", "carlos@email.com", "SKU-010", 1, new BigDecimal("150.00"));

        Order saved = repository.save(order);

        assertThat(saved.getId()).isNotNull();

        Optional<Order> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("Carlos");
        assertThat(found.get().getCustomerEmail()).isEqualTo("carlos@email.com");
        assertThat(found.get().getProductSku()).isEqualTo("SKU-010");
        assertThat(found.get().getQuantity()).isEqualTo(1);
        assertThat(found.get().getTotal()).isEqualByComparingTo("150.00");
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("deve retornar vazio para id inexistente")
    void shouldReturnEmptyForUnknownId() {
        Optional<Order> found = repository.findById(9999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("deve fazer rollback automático após cada teste")
    void shouldRollbackAfterTest() {
        assertThat(repository.count()).isZero();
    }
}
