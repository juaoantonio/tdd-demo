package br.dev.joaobarbosa.tdddemo.application.port;

import java.math.BigDecimal;

/**
 * Porta de saída (driven port) para processamento de pagamentos.
 * Segue o padrão Hexagonal Architecture: o domínio depende desta interface,
 * e não da implementação concreta do gateway externo.
 */
public interface PaymentGateway {

    /**
     * Cobra o valor do pedido no gateway de pagamento.
     *
     * @param orderId id interno do pedido
     * @param amount  valor a ser cobrado
     * @throws PaymentFailedException se o gateway recusar o pagamento
     */
    void charge(Long orderId, BigDecimal amount);

    /**
     * Estorna o pagamento de um pedido já cobrado.
     *
     * @param orderId id interno do pedido
     */
    void refund(Long orderId);
}
