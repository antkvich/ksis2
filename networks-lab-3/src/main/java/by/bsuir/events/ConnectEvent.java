package by.bsuir.events;

import by.bsuir.models.messages.ConnectMessage;

public class ConnectEvent extends Event {
    private final ConnectMessage connectMessage;

    public ConnectEvent(ConnectMessage connectMessage) {
        this.connectMessage = connectMessage;
    }

    public ConnectMessage getConnectMessage() {
        return connectMessage;
    }
}
