package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

public class MailSender {
    private final String smtpServerAdress;
    private final int stmpServerPort;

    public MailSender(String smtpServerAdress, int stmpServerPort) {
        this.smtpServerAdress = smtpServerAdress;
        this.stmpServerPort = stmpServerPort;
    }
    public void connect(){
    }

    public void sendMail(Mail mail){
    }
    public void close(){

    }
}
