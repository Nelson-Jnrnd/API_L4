package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ServerReplyCodesScanner {

    private final Scanner serverScanner;

    ServerReplyCodesScanner(InputStream inputStream) {
        serverScanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        // SMTP implementations must not recognize any other line termination than <CRLF>
        // see https://datatracker.ietf.org/doc/html/rfc5321#section-2.3.8
        serverScanner.useDelimiter("\r\n");
    }

    public ServerReplyCode nextCode() throws Exception {
        String receivedLine;
        do {
            receivedLine = serverScanner.next();
        } while (!isLast(receivedLine));
        ServerReplyCode code = ServerReplyCode.getByCode(Integer.parseInt(receivedLine.substring(0, 3)));
        if (code == null)
            throw new Exception("Unknown server reply code");
        return code;
    }

    private boolean isLast(String line) {
        return line.length() == 3 || line.charAt(3) != '-';
    }
}

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
