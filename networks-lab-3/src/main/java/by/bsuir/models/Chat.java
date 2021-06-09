package by.bsuir.models;

import by.bsuir.events.ConnectEvent;
import by.bsuir.events.DisconnectEvent;
import by.bsuir.events.EventHandler;
import by.bsuir.events.TextEvent;
import by.bsuir.models.server.Server;
import by.bsuir.models.server.services.History;

import java.io.IOException;
import java.net.InetAddress;

public class Chat {
    private Server server;
    private EventHandler<ConnectEvent> onConnected;
    private EventHandler<DisconnectEvent> onDisconnected;
    private EventHandler<TextEvent> onTextMessageSent;
    private EventHandler<TextEvent> onTextMessageReceived;

    public History connect(InetAddress address) throws IOException {
        this.server = new Server(this, address);
        return server.start();
    }

    public void disconnect() {
        server.stop();
    }

    public void sendTextMessage(String name, String text) {
        server.sendTextMessage(name, text);
    }

    public EventHandler<ConnectEvent> getOnConnected() {
        return onConnected;
    }

    public void setOnConnected(EventHandler<ConnectEvent> eventHandler) {
        this.onConnected = eventHandler;
    }

    public EventHandler<DisconnectEvent> getOnDisconnected() {
        return onDisconnected;
    }

    public void setOnDisconnected(EventHandler<DisconnectEvent> eventHandler) {
        this.onDisconnected = eventHandler;
    }

    public EventHandler<TextEvent> getOnTextMessageSent() {
        return onTextMessageSent;
    }

    public void setOnTextMessageSent(EventHandler<TextEvent> eventHandler) {
        this.onTextMessageSent = eventHandler;
    }

    public EventHandler<TextEvent> getOnTextMessageReceived() {
        return onTextMessageReceived;
    }

    public void setOnTextMessageReceived(EventHandler<TextEvent> messageEventHandler) {
        this.onTextMessageReceived = messageEventHandler;
    }
}
