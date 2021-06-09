import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {
    static Thread thread2;

    public static void main(String[] args) throws Exception {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
            System.out.println(interfaceAddresses.stream().map(InterfaceAddress::getBroadcast).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }
}
