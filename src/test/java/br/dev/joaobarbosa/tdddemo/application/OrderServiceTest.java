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
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private PaymentGateway paymentGateway;
    @Mock private StockPort stockPort;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
    }

    @Test
    @DisplayName("deve criar pedido com status CREATED")
    void shouldCreateOrderWithStatusCreated() {
        Order order = orderService.createOrder("João", "joao@email.com", "SKU-001", 2, new BigDecimal("100.00"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCustomerName()).isEqualTo("João");
        assertThat(order.getCustomerEmail()).isEqualTo("joao@email.com");
        assertThat(order.getProductSku()).isEqualTo("SKU-001");
        assertThat(order.getQuantity()).isEqualTo(2);
        assertThat(order.getTotal()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("deve chamar repository.save ao criar pedido")
    void shouldCallRepositorySaveWhenCreatingOrder() {
        orderService.createOrder("Maria", "maria@email.com", "SKU-002", 1, new BigDecimal("50.00"));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("deve rejeitar total <= 0")
    void shouldRejectTotalLessThanOrEqualToZero() {
        assertThatThrownBy(() -> orderService.createOrder("João", "joao@email.com", "SKU-001", 1, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total must be greater than zero");

        verifyNoInteractions(orderRepository);
    }

    @Test
    @DisplayName("deve rejeitar nome vazio ou nulo")
    void shouldRejectEmptyOrNullCustomerName() {
        assertThatThrownBy(() -> orderService.createOrder("", "joao@email.com", "SKU-001", 1, new BigDecimal("10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer name must not be empty");

        verifyNoInteractions(orderRepository);
    }

    @Test
    @DisplayName("deve rejeitar quantidade <= 0")
    void shouldRejectInvalidQuantity() {
        assertThatThrownBy(() -> orderService.createOrder("João", "joao@email.com", "SKU-001", 0, new BigDecimal("10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be at least 1");

        verifyNoInteractions(orderRepository);
    }

    @Test
    @DisplayName("deve buscar pedido por id")
    void shouldGetOrderById() {
        Order saved = new Order("Ana", "ana@email.com", "SKU-003", 1, new BigDecimal("200.00"));
        saved.setId(42L);
        when(orderRepository.findById(42L)).thenReturn(Optional.of(saved));

        Order found = orderService.getOrder(42L);

        assertThat(found.getId()).isEqualTo(42L);
        assertThat(found.getCustomerName()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("deve lançar OrderNotFoundException para id inexistente")
    void shouldThrowOrderNotFoundExceptionForUnknownId() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(99L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("99");
    }
}
