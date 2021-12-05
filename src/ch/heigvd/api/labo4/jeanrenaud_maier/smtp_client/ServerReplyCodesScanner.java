package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ServerReplyCodesScanner {
    private final Scanner serverScanner;

    ServerReplyCodesScanner(InputStream inputStream) {
        serverScanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        serverScanner.useDelimiter("\r\n");
    }

    public int nextCode() {
        String receivedLine;
        do {
            receivedLine = serverScanner.next();
        } while (!isLast(receivedLine));
        return Integer.parseInt(receivedLine.substring(0, 3));
    }

    private boolean isLast(String line) {
        return line.length() == 3 || line.charAt(3) != '-';
    }
}
