package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MailSender {
    private Socket socket;
    private Writer serverWriter;
    private ServerReplyCodesScanner serverReplyCodesScanner;

    public MailSender(String smtpServerAdress, int smtpServerPort) throws Exception {
        socket = new Socket(smtpServerAdress, smtpServerPort);
        serverWriter = new OutputStreamWriter(socket.getOutputStream());
        serverReplyCodesScanner = new ServerReplyCodesScanner(socket.getInputStream());

        checkServerReply(ServerReplyCode.SERVICE_READY);
        ehloCommand();
    }

    private void ehloCommand() throws Exception {
        sendLineToServer("EHLO " + InetAddress.getLocalHost().getHostAddress());
        checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
    }

    public void sendMail(Mail mail) throws Exception {
        sendLineToServer("MAIL FROM:" + angleBrackets(mail.getSender().getMailAddress()));
        checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
        for (Person recipient : mail.getRecipients()) {
            sendLineToServer("RCPT TO:" + angleBrackets(recipient.getMailAddress()));
            checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
        }

        sendLineToServer("DATA");
        checkServerReply(ServerReplyCode.START_MAIL_INPUT);
        sendLineToServer("From: " + mail.getSender());
        for (Person recipient : mail.getRecipients()) {
            sendLineToServer("To: " + recipient);
        }
        sendLineToServer("Subject: " + mail.getObject());

        sendLineToServer("");

        Scanner messageScanner = new Scanner(mail.getMessage());
        while (messageScanner.hasNextLine()) {
            String messageLine = messageScanner.nextLine();
            // see https://datatracker.ietf.org/doc/html/rfc5321#section-4.5.2
            if (messageLine.charAt(0) == '.') {
                messageLine = "." + messageLine;
            }
            sendLineToServer(messageLine);
        }

        sendLineToServer(".");
        checkServerReply(ServerReplyCode.MAIL_ACTION_OK);
    }

    public void close() throws Exception {
        sendLineToServer("QUIT");
        checkServerReply(ServerReplyCode.SERVICE_CLOSING);
    }

    private void sendLineToServer(String line) throws IOException {
        //lines end with CLRF, see https://datatracker.ietf.org/doc/html/rfc5321#section-2.3.8
        serverWriter.write(line + "\r\n");
        serverWriter.flush();
    }

    private void checkServerReply(ServerReplyCode expectedCode) throws Exception {
        if (serverReplyCodesScanner.nextCode() != expectedCode) {
            throw new Exception("Unexpected SMTP server reply");
        }
    }

    private static String angleBrackets(String text) {
        return "<" + text + ">";
    }
}
