package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


public class Person {
    @Getter
    private final String name;
    @Getter
    private final String mailAddress;

    public Person(@NonNull String name, @NonNull String mailAddress) {
        if (!mailAddress.matches("^(.+)@(\\S+)$"))
            throw new IllegalArgumentException("invalid mail address");
        if (name == "")
            throw new IllegalArgumentException("name can not be empty");
        this.name = name;
        this.mailAddress = mailAddress;
    }

    @Override
    public String toString() {
        return name + " <" + mailAddress + ">";
    }
}
