package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ReplyTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void shouldWorkWithOneLine() {
        Reply reply = new Reply(new StringReader("232 test test test\r\n"));
        assertEquals(232, reply.getCode());
    }@Test
    void shouldWorkWithMultipleLines() {
        Reply reply = new Reply(new StringReader(
                "100- test test test\r\n100-\r\n100-test bonjour\r\n100 test"));
        assertEquals(100, reply.getCode());
    }
}