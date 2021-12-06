package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MailSenderTest {
    private static final int PORT = 4242;
    private ServerSocket receptionistSocket;
    private Scanner socketScanner;
    private Writer socketWriter;

    MailSenderTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException {
        receptionistSocket = new ServerSocket(PORT);
    }

    @AfterEach
    void tearDown() throws IOException {
        receptionistSocket.close();
    }

    private void acceptClient() throws IOException {
        Socket socket = receptionistSocket.accept();
        socketScanner = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
        socketScanner.useDelimiter("\r\n");
        socketWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

        socketWriter.write("220 unit testing mock server ready\r\n");
        socketWriter.flush();
    }

    @Test
    void ehloShouldWork() throws IOException {
        new Thread(() -> {
            try {
                new MailSender("localhost", PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        acceptClient();

        String[] splittedClientCommand = socketScanner.next().split(" ", 2);
        assertEquals("EHLO", splittedClientCommand[0],
                "the EHLO command must begin with EHLO");
        assertTrue(splittedClientCommand[1].length() > 0,
                "the EHLO command must contain an argument");
    }

    @Test
    void sendMailShouldWork() {

    }
}