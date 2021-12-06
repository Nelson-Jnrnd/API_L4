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
    void codesShouldBeCorrect() throws Exception {
        PipedInputStream inputStream = new PipedInputStream();
        Writer writer = new OutputStreamWriter(new PipedOutputStream(inputStream), StandardCharsets.UTF_8);

        writer.write("354- test test\r\n");
        writer.write("354- \r\n");
        writer.write("354-\r\n");
        writer.write("354 a bonjour test\r\n");

        writer.write("250 a a\n a aaa\r\n");

        writer.write("220- hello\r\n");
        writer.write("220\r\n");

        writer.close();

        ServerReplyCodesScanner replyScanner = new ServerReplyCodesScanner(inputStream);
        assertEquals(ServerReplyCode.START_MAIL_INPUT, replyScanner.nextCode());
        assertEquals(ServerReplyCode.MAIL_ACTION_OK, replyScanner.nextCode());
        assertEquals(ServerReplyCode.SERVICE_READY, replyScanner.nextCode());
    }

}