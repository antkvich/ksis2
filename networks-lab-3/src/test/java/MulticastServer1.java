import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastServer1 {

    public static void main(String[] args) throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(53138);
        // Создание адреса группы и просоединение к ней
        InetAddress groupInetAddress = InetAddress.getByName("228.228.228.228");
        SocketAddress groupSocketAddress = new InetSocketAddress(groupInetAddress, 53138);
        multicastSocket.joinGroup(groupSocketAddress, NetworkInterface.getByName("wlp2s0"));
        // Создание буферного UDP-пакета
        byte[] bytes = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        // Получение
        System.out.println("+");
        multicastSocket.receive(datagramPacket);
        System.out.println("-");
        // Парсинг
        String message = new String(datagramPacket.getData(), StandardCharsets.UTF_8);
        System.out.println(message);
    }
}
