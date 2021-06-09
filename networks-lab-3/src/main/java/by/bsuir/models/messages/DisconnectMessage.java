package by.bsuir.models.messages;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class DisconnectMessage extends Message {

    public DisconnectMessage(InetAddress address, LocalDateTime time) {
        super(address, time);
    }
}
