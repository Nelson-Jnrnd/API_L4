package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

public class Mail {
    private final Person sender;
    private final Person[] recipients;
    private final String message;

    public Person getSender() {
        return sender;
    }
    public Person[] getRecipients() {
        return recipients;
    }

    public String getMessage() {
        return message;
    }

    public Mail(String message, Person sender , Person... recipients) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }
}