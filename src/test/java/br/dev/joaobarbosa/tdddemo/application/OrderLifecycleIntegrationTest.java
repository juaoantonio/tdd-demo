package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;
import br.dev.joaobarbosa.tdddemo.infra.stock.InMemoryStockAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Teste de integração do ciclo de vida completo do pedido.
 *
 * Usa Spring context real com:
 *   - H2 em memória (banco)
 *   - FakePaymentGateway (gateway)
 *   - InMemoryStockAdapter (estoque)
 *   - LogEmailSender (e-mail)
 *
 * Valida que todos os serviços funcionam em conjunto (wiring, transações, etc.)
 */
@SpringBootTest
@Transactional
class OrderLifecycleIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private InMemoryStockAdapter stockAdapter;

    @BeforeEach
    void setUp() {
        stockAdapter.setStock("SKU-001", 100);
    }

    @Test
    @DisplayName("fluxo completo: criar → pagar → estoque reservado → confirmação enviada")
    void shouldCreateAndPayOrder() {
        Order created = orderService.createOrder(
                "Lucas", "lucas@email.com", "SKU-001", 3, new BigDecimal("150.00"));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(OrderStatus.CREATED);

        int stockBefore = stockAdapter.getAvailable("SKU-001");

        Order paid = orderService.payOrder(created.getId());

        assertThat(paid.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(stockAdapter.getAvailable("SKU-001")).isEqualTo(stockBefore - 3);
    }

    @Test
    @DisplayName("fluxo completo: criar → cancelar (sem pagamento)")
    void shouldCreateAndCancelOrderWithoutPayment() {
        Order created = orderService.createOrder(
                "Sofia", "sofia@email.com", "SKU-001", 1, new BigDecimal("80.00"));

        Order cancelled = orderService.cancelOrder(created.getId());

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        // Estoque não foi reservado, portanto não deve ser liberado
        assertThat(stockAdapter.getAvailable("SKU-001")).isEqualTo(100);
    }

    @Test
    @DisplayName("fluxo completo: criar → pagar → cancelar → estoque liberado")
    void shouldCreatePayAndThenCancelOrder() {
        Order created = orderService.createOrder(
                "Rafael", "rafael@email.com", "SKU-001", 5, new BigDecimal("500.00"));
        orderService.payOrder(created.getId());
        assertThat(stockAdapter.getAvailable("SKU-001")).isEqualTo(95);

        Order cancelled = orderService.cancelOrder(created.getId());

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(stockAdapter.getAvailable("SKU-001")).isEqualTo(100); // liberou
    }

    @Test
    @DisplayName("não deve pagar pedido já pago")
    void shouldNotPayAlreadyPaidOrder() {
        Order created = orderService.createOrder(
                "Bia", "bia@email.com", "SKU-001", 1, new BigDecimal("50.00"));
        orderService.payOrder(created.getId());

        assertThatThrownBy(() -> orderService.payOrder(created.getId()))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    @DisplayName("não deve cancelar pedido já cancelado")
    void shouldNotCancelAlreadyCancelledOrder() {
        Order created = orderService.createOrder(
                "Tom", "tom@email.com", "SKU-001", 1, new BigDecimal("30.00"));
        orderService.cancelOrder(created.getId());

        assertThatThrownBy(() -> orderService.cancelOrder(created.getId()))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    @DisplayName("deve lançar InsufficientStockException quando estoque insuficiente")
    void shouldThrowWhenStockIsInsufficient() {
        stockAdapter.setStock("SKU-001", 1);

        Order created = orderService.createOrder(
                "Ana", "ana@email.com", "SKU-001", 5, new BigDecimal("250.00"));

        assertThatThrownBy(() -> orderService.payOrder(created.getId()))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("SKU-001");
    }
}
