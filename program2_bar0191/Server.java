// Server
// Referenced from source: https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
// Brandon Reid - CSCE3530
// Bar0191
// Program 2 - This Server is used from Program 1

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import java.lang.StringBuilder;
import java.net.MalformedURLException;

// main server class
public class Server {
    // main
    public static void main(String args[])  {

        // check for proper arugement length
        if (args.length < 1 || args.length > 1) {
            System.out.println("ERROR: Invalid arguments: java Server port");
            System.exit(0);
        }

        // check for proper port range
        if (Integer.parseInt(args[0]) < 1024 || Integer.parseInt(args[0]) > 49151) {
            System.out.println("ERROR: Invalid port: 1024 - 49151");
            System.exit(0);
        }

        // initialize socket and server socket
        Socket       sock  = null;
        ServerSocket sSock = null;

        // create new server socket
        try{
            sSock = new ServerSocket(Integer.parseInt(args[0]));
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error: use valid port number");
        }

        // Prompt that server is up and listening
        System.out.println("Server Listening on port " + Integer.parseInt(args[0]) + "...");

        // continues loop for continues connections
        while(true){
            try{
                sock = sSock.accept(); // accept connections
                System.out.print("Connection established: ");
                ServerThread sThread = new ServerThread(sock);
                sThread.start(); // create and start seperate server thread

            } catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");
            }
        }

    }
}

// ServerThread class for multiple client connections using Threads
class ServerThread extends Thread {  

    // initialize buffer, socket, and IO streams
    String         buffer = null;
    BufferedReader input  = null;
    PrintWriter    output = null;
    Socket         sock   = null;

    // constructor
    public ServerThread(Socket sock) {
        this.sock = sock;
    }

    // Helper method.
    // Read a stream to the end into a string.
    // referenced from: https://stackoverflow.com/questions/45415145/https-get-http-1-1-request-through-java-socket
    public String convertResponseToString(InputStream input) throws IOException {
    	String pageContent = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = input.read(buffer)) > 0) {
            output.write(buffer, 0, bytesRead);
        }

        pageContent = output.toString(StandardCharsets.UTF_8.name());

        return pageContent;
    }

    
    // Socket-based HTTP GET Request
    // referenced from: https://stackoverflow.com/questions/45415145/https-get-http-1-1-request-through-java-socket
    public void getRequest(String url) throws IOException {
        int PORT = 80;

        try (Socket sock = new Socket(url, PORT)) {
            String request = "GET / HTTP/1.1\r\nConnection: close\r\nHost:"+url+"\r\n\r\n";

            OutputStream output = sock.getOutputStream();
            output.write(request.getBytes(StandardCharsets.US_ASCII));

            InputStream input = sock.getInputStream();
            String response = convertResponseToString(input);

            System.out.println(response);
        }
    }

    public void validateURL(String buffer) {
    	try {
    		URL url = new URL("http://" + buffer);
    	} catch (MalformedURLException e) {
    		System.out.println("ERROR: validating URL");
    	}
    }

    // Thread run()
    public void run() {
        // initialize socket IO 
        try {
            input  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            output = new PrintWriter(sock.getOutputStream());
        } catch(IOException e){
            System.out.println("IO ERROR: server thread");
        }

        // prompt success of thread opening
        System.out.println("Client " + this.getName() + " Opened");

        // manage connection
        try {
            // read input buffer
            buffer = input.readLine();
            // while message is not bye, read and write to socket stream
            while(buffer.toLowerCase().compareTo("exit") != 0){
                System.out.println("Response from Client  ->  " + buffer);
                output.println(buffer);
                output.flush(); // flush buffer from extra characters
                //validateURL(buffer);
                getRequest(buffer);
                System.out.println("Response to Client    ->  " + buffer);
                buffer = input.readLine();
            }   
        } catch (IOException e) {
            buffer = this.getName();
            System.out.println("IO ERROR: Client " + buffer + " terminated abruptly");
        } catch(NullPointerException e){
            buffer = this.getName(); // display client termination 
            System.out.println("Connection terminated: Client " + buffer + " Closed");
        }
        // after client termination, close socket IO, and socket
        finally {    
            try {
                if (input != null) {
                    input.close(); 
                }
                if(output != null) {
                    output.close();
                }
                if (sock != null) {
                    sock.close();
                }
            } catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }
    }
}