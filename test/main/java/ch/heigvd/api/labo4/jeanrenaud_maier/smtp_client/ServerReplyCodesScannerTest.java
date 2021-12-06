package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ServerReplyCodesScannerTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void codesShouldBeCorrect() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        Writer writer = new OutputStreamWriter(new PipedOutputStream(inputStream), StandardCharsets.UTF_8);

        writer.write("123- test test\r\n");
        writer.write("123- \r\n");
        writer.write("123-\r\n");
        writer.write("123 a bonjour test\r\n");

        writer.write("343 a a\n a aaa\r\n");

        writer.write("654- hello\r\n");
        writer.write("654\r\n");

        writer.close();

        ServerReplyCodesScanner replyScanner = new ServerReplyCodesScanner(inputStream);
        assertEquals(123, replyScanner.nextCode());
        assertEquals(343, replyScanner.nextCode());
        assertEquals(654, replyScanner.nextCode());
    }

}