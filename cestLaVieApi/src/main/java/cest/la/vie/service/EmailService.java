package cest.la.vie.service;

import cest.la.vie.persistence.model.User;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class);

    private final Mailer mailer;

    @Inject
    public EmailService(Mailer mailer) {
        this.mailer = mailer;
    }

    public void sendVerificationEmail(User user, String sessionKey) {
        String verificationLink = "http://localhost:8080/user/verify?sessionKey=" + sessionKey;
        String htmlContent = "<p>Ciao " + user.getName() + ",</p>" +
                "<p>Grazie per esserti registrato. Clicca sul link per verificare il tuo account:</p>" +
                "<a href='" + verificationLink + "'>Verifica</a>" +
                "<p>Se non hai richiesto la registrazione, ignora questa email.</p>";

        try {
            mailer.send(Mail.withHtml(user.getEmail(),
                    "Conferma la tua registrazione",
                    htmlContent)
            );
            LOGGER.info("Email di verifica inviata con successo a: " + user.getEmail());
        } catch (Exception e) {
            LOGGER.error("Errore nell'invio dell'email di verifica a " + user.getEmail(), e);
        }
    }
}
