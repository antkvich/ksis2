package by.bsuir.models.server.services;

import by.bsuir.events.TextEvent;
import by.bsuir.models.messages.DisconnectMessage;
import by.bsuir.models.messages.TextMessage;
import by.bsuir.models.server.Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class TextService {
    private final Server server;

    public TextService(Server server) {
        this.server = server;
    }

    public void sendText(TextMessage textMessage) {
        // Сериализация
        byte[] bytes = textMessageToBytes(textMessage);
        // Рассылка
        synchronized (server.getConnections()) {
            for (Connection connection : server.getConnections()) {
                sendText(connection, bytes);
            }
        }
        // Регистрация события
        TextEvent textEvent = new TextEvent(textMessage);
        server.getChat().getOnTextMessageSent().handle(textEvent);
        server.getHistoryService().getHistory().add(textMessage);
    }

    public void sendText(Connection connection, TextMessage textMessage) {
        byte[] bytes = textMessageToBytes(textMessage);
        sendText(connection, bytes);
    }

    private void sendText(Connection connection, byte[] bytes) {
        try {
            OutputStream outputStream = connection.getSocket().getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(3);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            // Обрыв неисправного соединения
            DisconnectMessage disconnectMessage = new DisconnectMessage(server.getAddress(), LocalDateTime.now());
            server.getConnectionService().sendDisconnect(connection, disconnectMessage);
            // Сообщение о неполадках
            System.out.println("______________НЕИСПРАВНОЕ СОЕДИНЕНИЕ______________");
            e.printStackTrace();
            System.out.println("__________________________________________________");
        }
    }

    private byte[] textMessageToBytes(TextMessage textMessage) {
        try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(textMessage);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveText(Socket socket) {
        try {
            // Чтение
            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
            TextMessage textMessage = (TextMessage) objectInputStream.readObject();
            // Регистрация события
            TextEvent textEvent = new TextEvent(textMessage);
            server.getChat().getOnTextMessageReceived().handle(textEvent);
            server.getHistoryService().getHistory().add(textMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

