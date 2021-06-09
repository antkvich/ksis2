package by.bsuir.models.server.services;

import by.bsuir.events.ConnectEvent;
import by.bsuir.events.DisconnectEvent;
import by.bsuir.models.messages.ConnectMessage;
import by.bsuir.models.messages.DisconnectMessage;
import by.bsuir.models.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;

public class ConnectionService extends Thread {
    static final int CONNECTION_PORT = 45137;
    static final int TIMEOUT = 1500;
    private final Server server;

    public ConnectionService(Server server) {
        this.server = server;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            InetAddress address = server.getAddress();
            ServerSocket serverSocket = new ServerSocket(CONNECTION_PORT, 50, address);
            // Установка таймаута
            serverSocket.setSoTimeout(TIMEOUT);
            // Получения сервиса истории
            HistoryService historyService = server.getHistoryService();
            boolean isHistoryRequested = false;
            // Получение соединений с установленным таймаутом
            while (!isInterrupted()) {
                try {
                    // Получение сокета
                    Socket socket = serverSocket.accept();
                    // Установка соединения
                    establishConnection(socket, false);
                    // Запрос истории по первому установленному соединению
                    if (!isHistoryRequested) {
                        historyService.sendHistoryRequest(socket);
                        isHistoryRequested = true;
                    }
                } catch (SocketTimeoutException e) {
                    // Прерывание ожидания получения истории, если она ещё не была получена
                    historyService.interruptHistoryWait();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void establishConnection(Socket socket, boolean register) {
        // Создания потока прослушки соединения
        Connection connection = new Connection(server, socket);
        connection.start();
        // Добавление соединения в список соединений
        server.getConnections().add(connection);
        // Регистрация события
        if (register) {
            ConnectMessage connectMessage = new ConnectMessage(socket.getInetAddress(), LocalDateTime.now());
            if (server.getHistoryService().isHistoryAvailable()) {
                ConnectEvent connectEvent = new ConnectEvent(connectMessage);
                server.getChat().getOnConnected().handle(connectEvent);
            }
            server.getHistoryService().getHistory().add(connectMessage);
        }
    }

    public void sendDisconnect(DisconnectMessage disconnectMessage) {
        // Сериализация
        byte[] bytes = disconnectMessageToBytes(disconnectMessage);
        // Разрыв соединений
        synchronized (server.getConnections()) {
            for (Connection connection : server.getConnections()) {
                sendDisconnect(connection, bytes);
            }
        }
    }

    public void sendDisconnect(Connection connection, DisconnectMessage disconnectMessage) {
        byte[] bytes = disconnectMessageToBytes(disconnectMessage);
        sendDisconnect(connection, bytes);
        // Регистрация события
        if (server.getHistoryService().isHistoryAvailable()) {
            DisconnectEvent disconnectEvent = new DisconnectEvent(disconnectMessage);
            server.getChat().getOnDisconnected().handle(disconnectEvent);
        }
        server.getHistoryService().getHistory().add(disconnectMessage);
    }

    private void sendDisconnect(Connection connection, byte[] bytes) {
        try {
            OutputStream outputStream = connection.getSocket().getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(4);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
            // Обрыв соединения
            dropConnection(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] disconnectMessageToBytes(DisconnectMessage disconnectMessage) {
        try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(disconnectMessage);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveDisconnect(Connection connection) {
        try {
            // Чтение
            InputStream inputStream = connection.getSocket().getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
            DisconnectMessage disconnectMessage = (DisconnectMessage) objectInputStream.readObject();
            // Обрыв соединения
            dropConnection(connection);
            // Регистрация события
            DisconnectEvent disconnectEvent = new DisconnectEvent(disconnectMessage);
            server.getChat().getOnDisconnected().handle(disconnectEvent);
            server.getHistoryService().getHistory().add(disconnectMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void dropConnection(Connection connection) throws IOException {
        // Прерывание потока прослушки соединения
        connection.interrupt();
        // Удаление соединения из списка соединений
        server.getConnections().remove(connection);
        // Закрытие сокета
        connection.getSocket().close();
    }
}
