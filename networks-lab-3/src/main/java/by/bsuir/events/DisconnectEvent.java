package by.bsuir.events;

import by.bsuir.models.messages.DisconnectMessage;

public class DisconnectEvent extends Event{
    private final DisconnectMessage disconnectMessage;

    public DisconnectEvent(DisconnectMessage disconnectMessage) {
        this.disconnectMessage = disconnectMessage;
    }

    public DisconnectMessage getDisconnectMessage() {
        return disconnectMessage;
    }
}
