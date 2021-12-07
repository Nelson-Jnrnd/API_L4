package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

/**
 * Implements a minimal SMTP client able to send mails to multiple recipients and with non ASCII characters.
 */
public class MailSender implements AutoCloseable {
    private final Socket socket;
    private final Writer serverWriter;
    private final ServerReplyCodesScanner serverReplyCodesScanner;

    /**
     * Connects the client to an SMTP server and initiates communication
     *
     * @param smtpServerAddress address (ip or name) of the SMTP server
     * @param smtpServerPort    port of the SMTP server
     * @throws Exception if a communication error occurs or if the server behaves unexpectedly
     */
    public MailSender(String smtpServerAddress, int smtpServerPort) throws Exception {
        socket = new Socket(smtpServerAddress, smtpServerPort);
        serverWriter = new OutputStreamWriter(socket.getOutputStream());
        serverReplyCodesScanner = new ServerReplyCodesScanner(socket.getInputStream());

        checkServerReply(ServerReplyCode.SERVICE_READY);

        sendLineToServer("EHLO " + InetAddress.getLocalHost().getHostAddress());
        checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
    }

    /**
     * Send an e-mail using the SMTP server the client is connected to
     *
     * @param mail mail to send
     * @throws Exception if a communication error occurs or if the server behaves unexpectedly
     */
    public void sendMail(Mail mail) throws Exception {
        sendMailCommands(mail.getSender(), mail.getRecipients());
        sendMessageHeaders(mail);
        sendMessageContent(mail.getMessage());
    }

    /**
     * Initiates the mail sending process by sending MAIL FROM, RCPT TO and DATA commands
     *
     * @param sender     person whose mail address is used as MAIL FROM parameter
     * @param recipients person whose mail addresses are used as RCPT TO parameters
     * @throws Exception if a communication error occurs or if the server behaves unexpectedly
     */
    private void sendMailCommands(Person sender, Person[] recipients) throws Exception {
        sendLineToServer("MAIL FROM:" + surroundAngleBrackets(sender.getMailAddress()));
        checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
        for (Person recipient : recipients) {
            sendLineToServer("RCPT TO:" + surroundAngleBrackets(recipient.getMailAddress()));
            checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
        }

        sendLineToServer("DATA");
        checkServerReply(ServerReplyCode.START_MAIL_INPUT);
    }

    /**
     * Generates mail headers and sends them to the server
     *
     * @param mail mail used to generate headers
     * @throws IOException if a communication error occurs
     */
    private void sendMessageHeaders(Mail mail) throws IOException {
        sendLineToServer("From: " + headerEncode(mail.getSender().toString()));
        for (Person recipient : mail.getRecipients()) {
            sendLineToServer("To: " + headerEncode(recipient.toString()));
        }
        sendLineToServer("Subject: " + headerEncode(mail.getObject()));
        // the following headers enable sending non ASCII characters in message
        // see https://datatracker.ietf.org/doc/html/rfc2045
        sendLineToServer("MIME-Version: 1.0");
        sendLineToServer("Content-Type: text/plain; charset=utf-8");
        sendLineToServer("Content-Transfer-Encoding: 8bit");

        sendLineToServer("");
    }

    /**
     * Sends message content to the server. If a message line starts with '.', the client adds a second '.' at the
     * beginning of the line. (See https://datatracker.ietf.org/doc/html/rfc5321#section-4.5.2)
     *
     * @param messageContent text to send
     * @throws Exception if a communication error occurs or if the server behaves unexpectedly
     */
    private void sendMessageContent(String messageContent) throws Exception {
        Scanner messageScanner = new Scanner(messageContent);
        while (messageScanner.hasNextLine()) {
            String messageLine = messageScanner.nextLine();
            if (messageLine.charAt(0) == '.') {
                messageLine = "." + messageLine;
            }
            sendLineToServer(messageLine);
        }

        sendLineToServer(".");
        checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
    }

    /**
     * Ends the SMTP connection
     *
     * @throws Exception if a communication error occurs or if the server behaves unexpectedly
     */
    public void close() throws Exception {
        sendLineToServer("QUIT");
        checkServerReply(ServerReplyCode.SERVICE_CLOSING);
        socket.close();
    }


    private void sendLineToServer(String line) throws IOException {
        //lines end with CLRF, see https://datatracker.ietf.org/doc/html/rfc5321#section-2.3.8
        serverWriter.write(line + "\r\n");
        serverWriter.flush();
    }

    /**
     * Reads next server reply and throws exception if it is not the expected reply
     *
     * @param expectedCode expected reply code
     * @throws Exception if the reply code is different from expected
     */
    private void checkServerReply(ServerReplyCode expectedCode) throws Exception {
        if (serverReplyCodesScanner.nextCode() != expectedCode) {
            throw new Exception("Unexpected SMTP server reply");
        }
    }

    private static String surroundAngleBrackets(String text) {
        return "<" + text + ">";
    }

    /**
     * If necessary, encodes text according to RFC 2047 with UTF-8 and Base64
     *
     * @param text text to encode
     * @return the input text unmodified if it is pure ASCII, or encoded otherwise
     */
    private static String headerEncode(String text) {
        return isPureAscii(text) ?
                text :
                "=?UTF-8?B?" + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)) + "?=";
    }

    private static boolean isPureAscii(String text) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(text);
    }
}
