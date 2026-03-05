package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.application.port.EmailSender;
import br.dev.joaobarbosa.tdddemo.domain.Order;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailSender emailSender;

    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void notifyOrderConfirmed(Order order) {
        String subject = "Pedido #" + order.getId() + " confirmado!";
        String body = "Olá, " + order.getCustomerName() + "!\n\n"
                + "Seu pedido #" + order.getId() + " foi confirmado e pago com sucesso.\n"
                + "Produto: " + order.getProductSku() + " x" + order.getQuantity() + "\n"
                + "Total: R$ " + order.getTotal() + "\n\n"
                + "Obrigado pela compra!";
        emailSender.send(order.getCustomerEmail(), subject, body);
    }

    public void notifyOrderCancelled(Order order) {
        String subject = "Pedido #" + order.getId() + " cancelado";
        String body = "Olá, " + order.getCustomerName() + "!\n\n"
                + "Seu pedido #" + order.getId() + " foi cancelado.\n"
                + "Se você já realizou o pagamento, o estorno será processado em até 5 dias úteis.\n\n"
                + "Em caso de dúvidas, entre em contato com nosso suporte.";
        emailSender.send(order.getCustomerEmail(), subject, body);
    }
}
