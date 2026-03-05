package br.dev.joaobarbosa.tdddemo.application.port;

/**
 * Porta de saída para envio de e-mails.
 * A implementação concreta pode ser SMTP, SendGrid, SES ou um log/fake para testes.
 */
public interface EmailSender {

    /**
     * Envia um e-mail simples.
     *
     * @param to      endereço do destinatário
     * @param subject assunto do e-mail
     * @param body    corpo do e-mail (texto plano)
     */
    void send(String to, String subject, String body);
}
