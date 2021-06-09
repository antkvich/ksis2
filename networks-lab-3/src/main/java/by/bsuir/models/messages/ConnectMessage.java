package by.bsuir.models.messages;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class ConnectMessage extends Message {

    public ConnectMessage(InetAddress address, LocalDateTime time) {
        super(address, time);
    }
}
