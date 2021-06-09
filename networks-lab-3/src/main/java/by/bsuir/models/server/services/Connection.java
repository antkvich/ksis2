package by.bsuir.models.server.services;

import by.bsuir.models.server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class Connection extends Thread {
    private final Server server;
    private final Socket socket;

    public Connection(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        setDaemon(true);
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            while (!isInterrupted()) {
                read(inputStream);
            }
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(InputStream stream) throws IOException {
        int messageType = stream.read();
        switch (messageType) {
            case 1 -> handleHistoryRequest();
            case 2 -> handleHistoryResponse();
            case 3 -> handleText();
            case 4 -> handleDisconnect();
        }
    }

    private void handleHistoryRequest() {
        server.getHistoryService().receiveHistoryRequest(socket);
    }

    private void handleHistoryResponse() {
        server.getHistoryService().receiveHistoryResponse(socket);
    }

    private void handleText() {
        server.getTextService().receiveText(socket);
    }

    private void handleDisconnect() {
        server.getConnectionService().receiveDisconnect(this);
    }
}
