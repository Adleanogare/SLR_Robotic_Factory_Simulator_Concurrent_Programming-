import java.net.*;
import java.io.IOException;

public class Client {

    public static void main(String args[]) {
        try { 
            InetAddress netAddr = InetAddress.getByName("127.0.0.1");
            try {
                System.out.println("TryInet");
                SocketAddress sockAddr = new InetSocketAddress(netAddr, 64000);
                System.out.println("TrySocket");
                Socket socket = new Socket();
                socket.connect(sockAddr, 1000); // Timeout of 1000 ms
                System.out.println("Processor");
                Runnable reqProcessor = new RequestProcessor(socket, true);
                new Thread(reqProcessor).start();
            }
            catch (IOException ex) {
                System.out.println("Erreur");
                System.out.println(ex);
            }
        }
        catch (IOException ex) {
            System.out.println("Erreur connection");
        }
    }
}