package by.bsuir.models.server.services;

import by.bsuir.models.server.Server;

import java.io.IOException;
import java.net.*;

public class BroadcastService extends Thread {
    static final int BROADCAST_PORT = 45138;
    private final Server server;

    public BroadcastService(Server server) {
        this.server = server;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(BROADCAST_PORT);
            DatagramPacket packetBuffer = new DatagramPacket(new byte[1024], 1024);
            // Для установки соединения
            ConnectionService connectionService = server.getConnectionService();
            // Получение бродкастов
            while (!isInterrupted()) {
                try {
                    // Получение пакета
                    datagramSocket.receive(packetBuffer);
                    // Создание сокета
                    InetAddress address = packetBuffer.getAddress();
                    Socket socket = new Socket(address, ConnectionService.CONNECTION_PORT);
                    // Установка соединения
                    connectionService.establishConnection(socket, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcast() throws IOException {
        // Создание сокета
        InetAddress address = server.getAddress();
        DatagramSocket datagramSocket = new DatagramSocket(0, address);
        datagramSocket.setBroadcast(true);
        // Отправка
        String broadcastIp = getBroadcastIp(address.getHostName());
        InetAddress broadcastAddress = InetAddress.getByName(broadcastIp);
        DatagramPacket packet = new DatagramPacket(new byte[0], 0, broadcastAddress, BROADCAST_PORT);
        datagramSocket.send(packet);
        // Закрытие сокета
        datagramSocket.close();
    }

    private String getBroadcastIp(String ip) {
        StringBuilder builder = new StringBuilder(ip);
        while (builder.charAt(builder.length() - 1) != '.') {
            builder.setLength(builder.length() - 1);
        }
        return builder.append("255").toString();
    }
}
