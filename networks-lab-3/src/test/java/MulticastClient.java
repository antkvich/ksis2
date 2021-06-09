import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress inetAddress = InetAddress.getByName("192.168.0.105");
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, 0);
        DatagramSocket datagramSocket = new DatagramSocket(socketAddress);
        // Создание адреса группы
        InetAddress groupInetAddress = InetAddress.getByName("228.228.228.228");
        // Создание UDP-пакета
        byte[] bytes = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, groupInetAddress, 53138);
        // Отправка
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }
}
