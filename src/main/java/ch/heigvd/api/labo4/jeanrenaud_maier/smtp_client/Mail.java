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
    private final String object;

    public Mail(@NonNull String message, @NonNull Person sender, @NonNull String object, @NonNull Person... recipients) {
        this.sender = sender;
        this.object = object;
        this.recipients = recipients.clone();
        this.message = message;
    }

    public Person[] getRecipients() {
        return recipients.clone();
    }
}
