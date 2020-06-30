import java.net.*;
import java.io.*;
 
public class EchoServer2B {
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
                try (PrintWriter out = new PrintWriter(serverSocket.accept().getOutputStream(), true)) {
                    out.println("hello world!");
                } catch (IOException e) {
                    System.out.println("error echoing: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("error listening: " + e.getMessage());
        }
    }
}
