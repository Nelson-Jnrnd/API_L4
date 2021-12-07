package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonTest {
    @Test
    void shouldAcceptValidMailAddress() {
        new Person("a", "afds.sadfsda@asdf.asd.com");
    }

    @Test
    void shouldThrowExceptionIfInvalidMailAddress() {
        assertThrows(IllegalArgumentException.class, () -> new Person("a", "aaa.aa"));
    }
}