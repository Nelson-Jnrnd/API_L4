package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

public class Person {
    private final String name;
    private final String mailAdress;

    public String getName() {
        return name;
    }

    public String getMailAdress() {
        return mailAdress;
    }

    public Person(String name, String mailAdress) {
        this.name = name;
        this.mailAdress = mailAdress;
    }
}
