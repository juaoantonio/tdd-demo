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
        emailSender.send(
                order.getCustomerEmail(),
                "Pedido #" + order.getId() + " confirmado!",
                "Olá, " + order.getCustomerName() + "!\n\nSeu pedido #" + order.getId() + " foi confirmado e pago com sucesso.\nProduto: " + order.getProductSku() + " x" + order.getQuantity() + "\nTotal: R$ " + order.getTotal() + "\n\nObrigado pela compra!"
        );
    }

    public void notifyOrderCancelled(Order order) {
        emailSender.send(
                order.getCustomerEmail(),
                "Pedido #" + order.getId() + " cancelado",
                "Olá, " + order.getCustomerName() + "!\n\nSeu pedido #" + order.getId() + " foi cancelado.\nSe você já realizou o pagamento, o estorno será processado em até 5 dias úteis.\n\nEm caso de dúvidas, entre em contato com nosso suporte."
        );
    }
}
