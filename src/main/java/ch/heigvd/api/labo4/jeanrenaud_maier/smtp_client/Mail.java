package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import lombok.Getter;
import lombok.NonNull;

/**
 * Class that represents an e-mail that can be sent using the MailSender class
 */
public class Mail {
    @Getter
    private final Person sender;
    private final Person[] recipients;
    @Getter
    private final String message;
    @Getter
    private final String subject;

    public Mail(@NonNull String message, @NonNull Person sender, @NonNull String subject, @NonNull Person... recipients) {
        if (recipients.length < 1)
            throw new IllegalArgumentException("Need at least 1 recipient");
        this.sender = sender;
        this.subject = subject;
        this.recipients = recipients.clone();
        this.message = message;
    }

    public Person[] getRecipients() {
        return recipients.clone();
    }
}
