//HELP
import java.net.Socket;
import java.io.BufferedReader;
import java.io.*;



public class RequestProcessor implements Runnable {

    private Socket socket;
    private boolean client;

    public RequestProcessor(Socket socket, boolean client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            System.out.println(socket.getRemoteSocketAddress().toString());
            InputStream inpStr = socket.getInputStream();
            Reader strReader = new InputStreamReader(inpStr);
            BufferedReader buffReader = new BufferedReader(strReader);

            // - read and decode input request
            OutputStream outStr = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outStr, true); // Autoflush
            // - build and write response
            String s = null;
            if(client) {
                writer.println("I send HelloWorld!");
                System.out.println("I send HelloWorld!");
                while ((s=buffReader.readLine())!=null) {
                    System.out.println("I received back : " + s + " !");
                }
            } else {
                System.out.println("Waiting for a message");
                while(true) {
                    while ((s=buffReader.readLine())!=null) {
                        writer.println("I received : " + s + " !");
                        System.out.println("I received : " + s + " !");
                    }
                }
            }


        }

        catch (IOException ex) {
            if (client) {
                System.out.println("Erreur request client");
                System.out.println(ex);
            } else {
                System.out.println("Erreur request serveur");
                System.out.println(ex);
            }
            
        }
        
    }
}