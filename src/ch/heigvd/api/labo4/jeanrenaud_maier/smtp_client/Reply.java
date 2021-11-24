package ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client;

import java.io.InputStream;
import java.io.Reader;

class Reply {
    private final int code;

    Reply(Reader replyReader) {
        code = -1;
    }

    public int getCode() {
        return code;
    }
}
