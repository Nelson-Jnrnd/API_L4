package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ReplyScanner {
    private Scanner scanner;

    ReplyScanner(InputStream inputStream) {
        scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        scanner.useDelimiter("\r\n");
    }

    public int nextCode() {
        int code;
        String line;
        do {
            line = scanner.next();
            code = Integer.parseInt(line.substring(0, 3));
        } while (isLast(line));
        return code;
    }

    private boolean isLast(String line) {
        return line.length() > 3 && line.charAt(3) == '-';
    }
}
