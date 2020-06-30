import java.net.*;
import java.io.*;
 
public class EchoServer2C {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        run(portNumber);
    }

    private static void run(int portNumber) {
        System.err.println("Running on port " + portNumber);
         
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try (
                    Socket clientSocket = serverSocket.accept();     
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        out.println(inputLine.toUpperCase());
                    }
                } catch (IOException e) {
                    System.out.println("error echoing: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("error listening: " + e.getMessage());
        }
    }
}
