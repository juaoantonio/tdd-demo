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
        // TODO: not yet implemented
        throw new UnsupportedOperationException("notifyOrderConfirmed not yet implemented");
    }

    public void notifyOrderCancelled(Order order) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("notifyOrderCancelled not yet implemented");
    }
}
