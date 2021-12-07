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

        sendLineToSocket("220 unit testing mock server ready");
    }

    private void ehloReply() throws IOException {
        socketScanner.next();
        sendLineToSocket("250 unit testing mock server welcome");
    }

    private void sendLineToSocket(String line) throws IOException {
        socketWriter.write(line + "\r\n");
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
    void sendMailShouldWork() throws IOException {
        new Thread(() -> {
            try {
                Mail mail = new Mail(
                        "Message\n.\n.test",
                        new Person("sender", "a@a.com"),
                        "object",
                        new Person("rec1", "b@b.com"),
                        new Person("rec2", "c@c.com"),
                        new Person("rec3", "d@d.com")
                );
                new MailSender("localhost", PORT).sendMail(mail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        acceptClient();
        ehloReply();

        assertEquals("MAIL FROM:<a@a.com>", socketScanner.next());
        sendLineToSocket("250 OK");

        assertEquals("RCPT TO:<b@b.com>", socketScanner.next());
        sendLineToSocket("250 OK");
        assertEquals("RCPT TO:<c@c.com>", socketScanner.next());
        sendLineToSocket("250 OK");
        assertEquals("RCPT TO:<d@d.com>", socketScanner.next());
        sendLineToSocket("250 OK");

        assertEquals("DATA", socketScanner.next());
        sendLineToSocket("354 a");

        assertEquals("From: sender <a@a.com>", socketScanner.next());
        assertEquals("To: rec1 <b@b.com>", socketScanner.next());
        assertEquals("To: rec2 <c@c.com>", socketScanner.next());
        assertEquals("To: rec3 <d@d.com>", socketScanner.next());
        assertEquals("Subject: object", socketScanner.next());
        assertEquals("MIME-Version: 1.0", socketScanner.next());
        assertEquals("Content-Type: text/plain; charset=utf-8", socketScanner.next());
        assertEquals("Content-Transfer-Encoding: 8bit", socketScanner.next());
        assertEquals("", socketScanner.next());

        assertEquals("Message", socketScanner.next());
        assertEquals("..", socketScanner.next());
        assertEquals("..test", socketScanner.next());
        assertEquals(".", socketScanner.next());

        sendLineToSocket("250 OK");
    }

    @Test
    void closeShouldWork() throws IOException {
        new Thread(() -> {
            try {
                new MailSender("localhost", PORT).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        acceptClient();
        ehloReply();

        assertEquals("QUIT", socketScanner.next());
        sendLineToSocket("221 bye");

    }
}