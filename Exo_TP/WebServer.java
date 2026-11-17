import java.net.*;
import java.io.IOException;

public class WebServer {
    public static void main(String args[]) {
        try {
            InetAddress specificAddr = InetAddress.getByName("127.0.0.1");
            try (
                ServerSocket serverSocket = new ServerSocket(64000, 50, specificAddr);
            ) { do {
                try {
                    System.out.println(serverSocket.getInetAddress());
                    while(true) {
                        System.out.println("Waiting for a connexion...");
                        Socket socket = serverSocket.accept();
                        System.out.printf("Client connected: %s%n", socket.getRemoteSocketAddress());
                        Runnable reqProcessor = new RequestProcessor(socket, false);
                        new Thread(reqProcessor).start();
                    }
                }
                catch (IOException ex) {
                    System.out.println("Erreur");
                }
            } while (true); }
            catch (IOException ex) {
                System.out.println("Erreur");
                System.out.println(ex);
            }
        }
        catch (UnknownHostException ex) {
            System.out.println("Erreur");
            System.out.println(ex);
        }
    }
}