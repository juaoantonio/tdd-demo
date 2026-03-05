package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("deve criar e buscar pedido com wiring completo do Spring")
    void shouldCreateAndRetrieveOrder() {
        Order created = orderService.createOrder("Fernanda", "fernanda@email.com", "SKU-100", 3, new BigDecimal("300.00"));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(created.getCustomerEmail()).isEqualTo("fernanda@email.com");
        assertThat(created.getProductSku()).isEqualTo("SKU-100");
        assertThat(created.getQuantity()).isEqualTo(3);

        Order found = orderService.getOrder(created.getId());
        assertThat(found.getCustomerName()).isEqualTo("Fernanda");
        assertThat(found.getTotal()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar id inexistente no contexto integrado")
    void shouldThrowWhenOrderNotFound() {
        assertThatThrownBy(() -> orderService.getOrder(-1L))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
