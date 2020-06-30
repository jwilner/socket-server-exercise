import java.io.*;
import java.net.*;

public class EchoClient3A {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        run(hostName, portNumber);
    }

    private static void run(String hostName, int portNumber) {
        System.err.println("Connecting to " + hostName + ":" + portNumber);

        try (
                // construct a socket with hostname and port
                Socket echoSocket = new Socket(hostName, portNumber);

                // get an output stream for writing to
                OutputStream out = echoSocket.getOutputStream();

                // get an input stream for reading from
                InputStream in = echoSocket.getInputStream();
            ) {
            // Create a writer for easy writing to the out stream.
            PrintWriter writer = new PrintWriter(out, true);

            // wrap the input stream with a buffered reader to make it easy to read a whole line
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // write to server
            writer.println("foobar");

            String response = reader.readLine(); 

            // print server's response to terminal
            System.out.println("Response: " + response);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
