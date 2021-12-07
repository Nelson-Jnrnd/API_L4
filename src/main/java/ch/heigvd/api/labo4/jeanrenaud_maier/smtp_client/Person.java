package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


@AllArgsConstructor
public class Person {
    @Getter
    @NonNull
    private final String name;
    @Getter
    @NonNull
    private final String mailAddress;

    @Override
    public String toString() {
        return name + " <" + mailAddress + ">";
    }
}
