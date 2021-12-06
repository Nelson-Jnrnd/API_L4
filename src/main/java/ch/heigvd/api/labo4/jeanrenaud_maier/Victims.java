package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Person;

import java.util.List;

public class Victims {
    public void setVictims(List<Person> victims) {
        this.victims = victims;
    }

    List<Person> victims;

    public Victims(List<Person> victims) {
        this.victims = victims;
    }

    public List<Person> getVictims() {
        return victims;
    }
}
