package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

public class Person {
    private final String name;
    private final String mailAddress;

    public String getName() {
        return name;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public Person(String name, String mailAddress) {
        this.name = name;
        this.mailAddress = mailAddress;
    }

    @Override
    public String toString() {
        return name + " <" + mailAddress + ">";
    }
}
