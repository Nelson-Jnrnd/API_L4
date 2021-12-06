package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

public class Mail {
    private final Person sender;
    private final Person[] recipients;
    private final String message;
    private final String object;

    public Person getSender() {
        return sender;
    }
    public Person[] getRecipients() {
        return recipients.clone();
    }

    public String getMessage() {
        return message;
    }
    public String getObject() {
        return object;
    }
    public Mail(String message, Person sender, String object, Person... recipients) {
        this.sender = sender;
        this.object = object;   
        this.recipients = recipients;
        this.message = message;
    }
}
