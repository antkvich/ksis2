package by.bsuir.models.server;

import by.bsuir.models.Chat;
import by.bsuir.models.messages.DisconnectMessage;
import by.bsuir.models.messages.TextMessage;
import by.bsuir.models.server.services.*;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Server {
    private final Chat chat;
    private final InetAddress address;
    private ConnectionService connectionService;
    private BroadcastService broadcastService;
    private final HistoryService historyService = new HistoryService();
    private final TextService textService = new TextService(this);
    private final Set<Connection> connections = Collections.synchronizedSet(new LinkedHashSet<>());

    public Server(Chat chat, InetAddress address) {
        this.chat = chat;
        this.address = address;
    }

    public History start() throws IOException {
        // Запуск сервиса соединений
        connectionService = new ConnectionService(this);
        connectionService.start();
        // Отправка бродкаста
        broadcastService = new BroadcastService(this);
        broadcastService.sendBroadcast();
        // Ожидание появления истории
        historyService.waitHistory();
        History history = historyService.getHistory();
        // Запуск сервиса бродкастов
        broadcastService.start();
        // Возврат истории сети
        return history;
    }

    public void stop() {
        // Остановка сервиса бродкастов
        broadcastService.interrupt();
        // Разрыв всех соединений
        sendDisconnectMessage();
        // Остановка сервиса соединений
        connectionService.interrupt();
    }

    public void sendDisconnectMessage() {
        DisconnectMessage disconnectMessage = new DisconnectMessage(address, LocalDateTime.now());
        connectionService.sendDisconnect(disconnectMessage);
    }

    public void sendTextMessage(String name, String text) {
        TextMessage textMessage = new TextMessage(address, LocalDateTime.now(), name, text);
        textService.sendText(textMessage);
    }

    public Chat getChat() {
        return chat;
    }

    public InetAddress getAddress() {
        return address;
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public BroadcastService getBroadcastService() {
        return broadcastService;
    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public TextService getTextService() {
        return textService;
    }

    public Set<Connection> getConnections() {
        return connections;
    }
}
