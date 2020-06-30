# sockets

Sockets are how processes communicate over networks; nearly everything that happens on the internet happens via sockets. 

This a multipart programming exercise introducing sockets and network communication. 

1) In the first part, you'll experiment with `netcat` (`nc`) and learn how to play with sockets
2) Then you'll write a socket **server** that can talk to `nc`
3) Finally, you'll write a socket **client** that can fill the part of `nc` -- doing all the socket work yourself!

Each part will have prompts, stubs, and follow ups; finally, if you get stuck, there will also be solutions at the end.

We've borrowed much of the code from Oracle's tutorial on [sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html).

*Note: although the Java should work everywhere, the examples here will be for a unix-y shell, so you'll only be able to run them word-for-word on mac or linux, but it shouldn't be too hard to translate to Windows*

## background: how sockets work

Every time you load a web page on the internet, your browser opens up a socket (let's call it socket A, often called a client socket) and sends data over it to another computer where a web server is running. The web server is already listening on a socket (socket B, called a server socket) of its own, and, when it _accepts_ a new connection request, it gets a new socket (socket C, also called a client socket) to talk to socket A (and the process behind it). Socket B is special and only receives new connection requests, but socket A and socket C both support sending and receiving data.

In network communications, we usually call the side that initiates the connection the **client** and the listening / responding side the **server**. For the client to find the server, we can use an **address** with a name and a **port** number; the name tells you which computer (with the help of DNS and IP addresses), and the port tells you which socket on the computer.

## netcat

`nc` is a common command line utility that lets you communicate over networks.

We'll use it in two modes -- one listening as a server and another connecting as a client.

### listen with nc

To listen with `nc`, open up one terminal and run: 
```bash
> nc -l 8000
```

You shouldn't see anything, and the terminal should look like it's waiting -- that means `nc` is listening for traffic. In this window, `nc` is the **server**.

### connect with nc

Now, with the server still running, let's connect as a client. Open up another terminal window and run:

```bash
> nc localhost 8000
```

Again, the terminal should look like it's waiting -- this time though, it means the client has connected to the server and is waiting for you to send data.

To send data, just type anything and then hit enter (by default, `nc` only sends whole lines). When you type in the **client** window, you should see it output in the server window.

In the client:
```bash
> nc localhost 8000
hello world!
```

And then over the network in the server window:
```bash
...
hello world!
```

You can close both server and client by hitting `ctrl-c` or `ctrl-d`.

### follow-ups

- What happens if the server isn't running and you try to connect with the client?
- What happens if the server is running but the client tries to connect to a different port? Is this different from when it isn't running? Why or why not?
- What does `localhost` mean?
- `nc` operates on lines, but networks transmit data as bytes; how does `nc` know when it has a whole line?

## part 2: server

Now that we've seen how network communication works, let's write code that does the same thing plus a little bit more. 

When we're done with part 1, we'll have a server that takes in a message from `nc` and responds with the same message -- only in ALL CAPS.

### stub

You can use the code below to get started. It checks the command line arguments, parses the first one to an integer, and then provides that to a private static function, `run`. It should compile out of the box.

```java
import java.net.*;
import java.io.*;
 
public class EchoServer {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        run(portNumber)
    }

    private static void run(int portNumber) {
        System.err.println("Running on port " + portNumber);
        // TODO: listen on the port!
    }
}
```

Right now, when you run it, you should see this:

```bash
> javac EchoServer.java
> java EchoServer 8000
Running on port 8000
```

The server will exit immediately -- you won't have a chance to talk to it!

### task 2A: `hello world!`

As a first step, **modify the `run` method above so that when a client connects, the server responds with `hello world!`.**

You've got it working correctly if it works just like this:

1) In one window, start the server running:
```bash
> javac EchoServer.java
> java EchoServer 8000
Running on port 8000
```

2) In another, connect to it with `nc`:

```bash
> nc localhost 8000
```

3) The server sends `hello world!` onto the `nc` and both server and client stop.

#### hints

- You'll need to construct a [ServerSocket](https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html) from the port number and then call `accept` to get a [Socket](https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html) from it.
- The socket will have an [OutputStream](https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html) on it for sending data; use a [PrintWriter](https://docs.oracle.com/javase/7/docs/api/java/io/PrintWriter.html) to send whole lines.

### task 2B: make the server stay alive

The server above dies the moment it's sent its message. Instead, let's make it stay alive after every connection.

**Update the `run` method so that the server can be reused.**

You've got it working correctly if you can send `nc localhost 8000` to the server **several times** and each time, the server responds with `hello world!`

#### hints

- If you want something to run forever in Java (or until you interrupt it with `ctrl-c`), you want a `while (true)` loop.

### task 2C: accepting input

The server runs non-stop now, but it still doesn't read any data from the client -- it just sends the same data every time.

**Update the `run` method so that when you type a line into `nc`, the server responds back with the same message, but in ALL CAPS.**

Example:
```bash
> nc localhost 8000
hello
HELLO
good bye
GOOD BYE
```

#### hints

- You *write* to an `OutputStream`, and you *read* from an `InputStream`.
- To read new lines from the stream, you can wrap the stream with `new BufferedReader(new InputStreamReader(inputStream))`. The [BufferedReader](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html) has a method, `String readLine()`, that returns each line or null when the stream is finished.
- To make a string upper case, use `string.toUpperCase()`

### follow-ups

- Can you make the server include the time of every message?
- Can you make the server close the connection when the client sends `quit`.
- What other behaviors can you add to the server?

## part 3: client 

The client should take two arguments (a name and a port to find the server with) and will then send any line that you type to the server and print the server's response with the prefix `Response: `.

### stub

To start, let's make a file with the basic framework of a client and put it in `EchoClient.java`.

```java
import java.io.*;
import java.net.*;
 
public class EchoClient {
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
        // TODO: Connect to socket!
    }
}
```

As described above, the program takes two arguments: the name (a string) and the port (an integer). Together, the name and port will tell the client where to connect to the server. 

Right now, when you run it, it should just print `Connecting to ...` and stop. For example:

```bash
> javac EchoClient.java
> java EchoClient localhost 8000
Connecting to localhost:8000
```

### task 3A: send a fixed string

Let's actually connect. For the moment, we'll just send the same string -- later, we'll make it send any string the user wants. 

**Update the client's `run` method to connect to a socket at the provided address, send the string `foobar`, and then print the line it receives back, with a `Response: ` prefix.**

For example, when you use it with your server:

```bash
> javac EchoClient.java
> java EchoClient localhost 8000
Connecting to localhost:8000
Response: FOOBAR
```

#### Hints:

- You may have used a `PrintWriter`, an `InputStreamReader`, and a `BufferedReader` in the server -- they'll be useful here again!
- You can write to terminal with `System.out.println`

### task 3B: read lines from the terminal

It's not much fun to always send the same string though; let's change the client to behave like `nc` and send lines from the user.

**Update the client's `run` method so that it connects to the server, sends any lines the user inputs to the server, prints responses, and disconnects when the user hits `ctrl-d`**

Again, for example:

```bash
> javac EchoClient.java
> java EchoClient localhost 8000
Connecting to localhost:8000
Hello
Response: HELLO
Good bye
Response: GOOD BYE
```

#### hints:

- You've used `System.out` and `System.err`; user input is on `System.in`, and it's an `InputStream` just like others!

### follow-ups

- The client always waits for server responses; can you make it send all of its messages and only check for responses right before it closes the connection?

## Things to explore

- All of these examples always happen on `localhost` -- but you can do them from anywhere on your network. Look up your computer's internal IP (usually a number starting with 192, like 192.168.1.153), and try connecting to your server there.
- With each pair of request and response lines, you've actually implemented a very simple line based **protocol** on top of TCP and IP. Another line-based protocol is very often used on top of TCP/IP -- HTTP -- and it's what browsers use. Can you implement HTTP with your server so that you can open `http://localhost:8000` in your browser?
