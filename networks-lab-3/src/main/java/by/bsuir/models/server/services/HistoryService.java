package by.bsuir.models.server.services;

import java.io.*;
import java.net.Socket;

public class HistoryService {
    private final History history = new History();
    private volatile boolean isHistoryAvailable;

    public void sendHistoryRequest(Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(1);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendHistoryResponse(Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(2);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(history);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveHistoryRequest(Socket socket) {
        sendHistoryResponse(socket);
    }

    public void receiveHistoryResponse(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            History externalHistory = (History) objectInputStream.readObject();
            // Вставка полученной истории
            history.importExternalHistory(externalHistory);
            // Уведомление о том, что история получена
            isHistoryAvailable = true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void waitHistory() {
        while (!isHistoryAvailable) {
            Thread.onSpinWait();
        }
    }

    public void interruptHistoryWait() {
        // Уведомление о том, что история не будет получена
        isHistoryAvailable = true;
    }

    public boolean isHistoryAvailable() {
        return isHistoryAvailable;
    }

    public History getHistory() {
        return history;
    }
}
