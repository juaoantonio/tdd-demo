package br.dev.joaobarbosa.tdddemo.infra.notification;

import br.dev.joaobarbosa.tdddemo.application.port.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de e-mail que apenas loga as mensagens (sem SMTP real).
 * Em produção, trocar por SendGrid, SES ou JavaMailSender sem tocar no domínio.
 */
@Component
public class LogEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LogEmailSender.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("[EMAIL] To: {} | Subject: {} | Body: {}", to, subject, body);
    }
}
