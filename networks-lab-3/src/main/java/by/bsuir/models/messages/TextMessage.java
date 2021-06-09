package by.bsuir.models.messages;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class TextMessage extends Message {
    private final String name;
    private final String text;

    public TextMessage(InetAddress address, LocalDateTime time, String name, String text) {
        super(address, time);
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
