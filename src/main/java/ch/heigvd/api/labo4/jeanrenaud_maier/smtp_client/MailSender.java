package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.server.ExportException;

public class MailSender {
    private Socket socket;
    private Writer serverWriter;
    private ServerReplyCodesScanner serverReplyCodesScanner;

    public MailSender(String smtpServerAdress, int smtpServerPort) throws Exception {
        socket = new Socket(smtpServerAdress, smtpServerPort);
        serverWriter = new OutputStreamWriter(socket.getOutputStream());
        serverReplyCodesScanner = new ServerReplyCodesScanner(socket.getInputStream());

        checkServerReply(220);
        ehloCommand();
    }
    private void ehloCommand() throws Exception {
        sendLineToServer("EHLO " + InetAddress.getLocalHost().getHostAddress());
        checkServerReply(250);
    }

    public void sendMail(Mail mail){
    }
    public void close(){

    }

    private void sendLineToServer(String line) throws IOException {
        serverWriter.write(line + "\r\n");
        serverWriter.flush();
    }

    private void checkServerReply(int expectedCode) throws Exception {
        if (serverReplyCodesScanner.nextCode() != expectedCode) {
            throw new Exception("Unexpected SMTP server reply");
        }
    }
}
