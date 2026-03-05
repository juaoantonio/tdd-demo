package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.application.port.EmailSender;
import br.dev.joaobarbosa.tdddemo.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private NotificationService notificationService;

    private Order buildOrder() {
        Order o = new Order("Clara", "clara@email.com", "SKU-001", 1, new BigDecimal("79.90"));
        o.setId(7L);
        return o;
    }

    @Test
    @DisplayName("deve enviar email para o endereço do cliente ao confirmar pedido")
    void shouldSendEmailToCustomerOnConfirmation() {
        notificationService.notifyOrderConfirmed(buildOrder());

        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(toCaptor.capture(), any(), any());

        assertThat(toCaptor.getValue()).isEqualTo("clara@email.com");
    }

    @Test
    @DisplayName("deve incluir id do pedido no email de confirmação")
    void shouldIncludeOrderIdInConfirmationEmail() {
        notificationService.notifyOrderConfirmed(buildOrder());

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(any(), any(), bodyCaptor.capture());

        assertThat(bodyCaptor.getValue()).contains("7");
    }

    @Test
    @DisplayName("deve enviar email de cancelamento para o cliente")
    void shouldSendCancellationEmailToCustomer() {
        notificationService.notifyOrderCancelled(buildOrder());

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(any(), subjectCaptor.capture(), any());

        assertThat(subjectCaptor.getValue().toLowerCase()).containsAnyOf("cancel", "cancelado");
    }

    @Test
    @DisplayName("deve chamar emailSender exatamente uma vez ao confirmar")
    void shouldCallEmailSenderExactlyOnceOnConfirmation() {
        notificationService.notifyOrderConfirmed(buildOrder());

        verify(emailSender, times(1)).send(any(), any(), any());
    }
}
