package br.dev.joaobarbosa.tdddemo.infra.payment;

import br.dev.joaobarbosa.tdddemo.application.port.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Gateway de pagamento simulado para desenvolvimento e testes.
 * Em produção, este adaptador seria substituído por uma integração real
 * (Stripe, PagSeguro, Mercado Pago, etc.) sem alterar o domínio.
 */
@Component
public class FakePaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(FakePaymentGateway.class);

    @Override
    public void charge(Long orderId, BigDecimal amount) {
        log.info("[FAKE PAYMENT] Charging R$ {} for order {}", amount, orderId);
        // Simula latência e sempre aprova o pagamento
    }

    @Override
    public void refund(Long orderId) {
        log.info("[FAKE PAYMENT] Refunding order {}", orderId);
    }
}
