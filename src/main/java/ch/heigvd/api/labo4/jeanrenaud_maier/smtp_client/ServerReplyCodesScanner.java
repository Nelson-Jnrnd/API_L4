package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import ch.heigvd.api.labo4.jeanrenaud_maier.PrankApplication;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A parser that reads reply codes from an SMTP server
 */
class ServerReplyCodesScanner {

    private static final Logger LOG = Logger.getLogger(PrankApplication.class.getName());

    private final Scanner serverScanner;

    /**
     * @param inputStream stream from where the SMTP server replies must be red
     */
    ServerReplyCodesScanner(InputStream inputStream) {
        serverScanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        // SMTP implementations must not recognize any other line termination than <CRLF>
        // see https://datatracker.ietf.org/doc/html/rfc5321#section-2.3.8
        serverScanner.useDelimiter("\r\n");
    }

    /**
     * Reads the next server reply and identify the reply code
     *
     * @return the reply code
     * @throws Exception if a communication error occurs or if the server behaves unexpectedly
     */
    public ServerReplyCode nextCode() throws Exception {
        String receivedLine;
        do {
            receivedLine = serverScanner.next();
            LOG.log(Level.INFO, "Server : " + receivedLine);
        } while (!isLast(receivedLine));
        ServerReplyCode code = ServerReplyCode.getByCode(Integer.parseInt(receivedLine.substring(0, 3)));
        if (code == null)
            throw new Exception("Unknown server reply code");
        return code;
    }

    /**
     * Checks if a line is the last line of a server reply
     *
     * @param line line received from SMTP server
     * @return false if other lines will come after this line in the same server reply, true otherwise
     */
    private boolean isLast(String line) {
        return line.length() == 3 || line.charAt(3) != '-';
    }
}

/**
 * Possible reply codes that can be sent by the SMTP server
 */
enum ServerReplyCode {
    SERVICE_READY(220), MAIL_ACTION_OK(250), START_MAIL_INPUT(354), SERVICE_CLOSING(221);

    private final int code;

    ServerReplyCode(int code) {
        this.code = code;
    }

    static ServerReplyCode getByCode(int code) {
        for (ServerReplyCode value : values())
            if (value.code == code)
                return value;
        return null;
    }
}
