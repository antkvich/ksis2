package by.bsuir.models.messages;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class Message implements Comparable<Message>, Serializable {
    protected InetAddress address;
    protected LocalDateTime time;

    protected Message(InetAddress address, LocalDateTime time) {
        this.address = address;
        this.time = time;
    }

    public InetAddress getAddress() {
        return address;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public int compareTo(Message m) {
        return time.compareTo(m.time);
    }
}
