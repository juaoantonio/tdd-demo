package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.application.port.PaymentGateway;
import br.dev.joaobarbosa.tdddemo.application.port.StockPort;
import br.dev.joaobarbosa.tdddemo.domain.Order;
import br.dev.joaobarbosa.tdddemo.domain.OrderRepository;
import br.dev.joaobarbosa.tdddemo.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServicePaymentTest {

    @Mock private OrderRepository orderRepository;
    @Mock private PaymentGateway paymentGateway;
    @Mock private StockPort stockPort;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private Order createdOrder;

    @BeforeEach
    void setUp() {
        createdOrder = new Order("João", "joao@email.com", "SKU-001", 2, new BigDecimal("199.90"));
        createdOrder.setId(1L);
        lenient().when(orderRepository.findById(1L)).thenReturn(Optional.of(createdOrder));
        lenient().when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ─── payOrder ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deve alterar status do pedido para PAID ao pagar")
    void shouldTransitionOrderStatusToPaid() {
        Order paid = orderService.payOrder(1L);

        assertThat(paid.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("deve cobrar o valor no gateway de pagamento ao pagar")
    void shouldChargePaymentGatewayOnPay() {
        orderService.payOrder(1L);

        verify(paymentGateway).charge(1L, new BigDecimal("199.90"));
    }

    @Test
    @DisplayName("deve reservar estoque no momento do pagamento")
    void shouldReserveStockOnPay() {
        orderService.payOrder(1L);

        verify(stockPort).reserve("SKU-001", 2);
    }

    @Test
    @DisplayName("deve enviar notificação de confirmação ao pagar")
    void shouldSendConfirmationNotificationOnPay() {
        orderService.payOrder(1L);

        verify(notificationService).notifyOrderConfirmed(createdOrder);
    }

    @Test
    @DisplayName("deve salvar pedido atualizado após pagamento")
    void shouldSaveOrderAfterPayment() {
        orderService.payOrder(1L);

        verify(orderRepository).save(createdOrder);
    }

    @Test
    @DisplayName("deve lançar InvalidOrderStatusException ao pagar pedido que não está CREATED")
    void shouldThrowWhenPayingNonCreatedOrder() {
        createdOrder.setStatus(OrderStatus.PAID);

        assertThatThrownBy(() -> orderService.payOrder(1L))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("pay");
    }

    // ─── cancelOrder ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("deve alterar status do pedido para CANCELLED ao cancelar")
    void shouldTransitionOrderStatusToCancelled() {
        Order cancelled = orderService.cancelOrder(1L);

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("deve enviar notificação de cancelamento ao cancelar")
    void shouldSendCancellationNotificationOnCancel() {
        orderService.cancelOrder(1L);

        verify(notificationService).notifyOrderCancelled(createdOrder);
    }

    @Test
    @DisplayName("deve fazer estorno no gateway ao cancelar pedido já pago")
    void shouldRefundPaymentGatewayWhenCancellingPaidOrder() {
        createdOrder.setStatus(OrderStatus.PAID);

        orderService.cancelOrder(1L);

        verify(paymentGateway).refund(1L);
    }

    @Test
    @DisplayName("deve liberar estoque ao cancelar pedido já pago")
    void shouldReleaseStockWhenCancellingPaidOrder() {
        createdOrder.setStatus(OrderStatus.PAID);

        orderService.cancelOrder(1L);

        verify(stockPort).release("SKU-001", 2);
    }

    @Test
    @DisplayName("não deve estornar nem liberar estoque ao cancelar pedido ainda CREATED")
    void shouldNotRefundOrReleaseStockWhenCancellingCreatedOrder() {
        orderService.cancelOrder(1L);

        verifyNoInteractions(paymentGateway);
        verifyNoInteractions(stockPort);
    }

    @Test
    @DisplayName("deve lançar InvalidOrderStatusException ao cancelar pedido já cancelado")
    void shouldThrowWhenCancellingAlreadyCancelledOrder() {
        createdOrder.setStatus(OrderStatus.CANCELLED);

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("cancel");
    }
}
