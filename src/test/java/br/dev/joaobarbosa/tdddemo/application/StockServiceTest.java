package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.application.port.StockPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockPort stockPort;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("deve delegar reserva para o StockPort")
    void shouldDelegateReserveToPort() {
        stockService.reserve("SKU-001", 5);

        verify(stockPort).reserve("SKU-001", 5);
    }

    @Test
    @DisplayName("deve delegar liberação para o StockPort")
    void shouldDelegateReleaseToPort() {
        stockService.release("SKU-002", 3);

        verify(stockPort).release("SKU-002", 3);
    }

    @Test
    @DisplayName("deve propagar InsufficientStockException do port")
    void shouldPropagateInsufficientStockException() {
        doThrow(new InsufficientStockException("SKU-001", 10, 2))
                .when(stockPort).reserve("SKU-001", 10);

        assertThatThrownBy(() -> stockService.reserve("SKU-001", 10))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("SKU-001");
    }

    @Test
    @DisplayName("deve rejeitar reserva de quantidade <= 0")
    void shouldRejectInvalidReserveQuantity() {
        assertThatThrownBy(() -> stockService.reserve("SKU-001", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");

        verifyNoInteractions(stockPort);
    }

    @Test
    @DisplayName("deve rejeitar liberação de quantidade <= 0")
    void shouldRejectInvalidReleaseQuantity() {
        assertThatThrownBy(() -> stockService.release("SKU-001", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");

        verifyNoInteractions(stockPort);
    }
}
