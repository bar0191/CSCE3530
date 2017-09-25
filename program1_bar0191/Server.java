// Server
// Referenced from source: https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
// Brandon Reid - CSCE3530
// Bar0191

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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

    // vowelCounter Method
    public String vowelCounter(String buffer) {
        // initialize variables
        int count = 0;
        String buff = buffer.toLowerCase();

        // loop through buffer string
        for (int i = 0; i < buff.length(); ++i) {
            // for each case, increment counter
            switch (buff.charAt(i)) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                    count++;
                default:
                    // no default
            }
        }
        return "The number of vowels present is " + count;
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
            while(buffer.toLowerCase().compareTo("bye") != 0){
                System.out.println("Response from Client  ->  " + buffer);
                output.println(vowelCounter(buffer));
                output.flush(); // flush buffer from extra characters
                System.out.println("Response to Client    ->  " + vowelCounter(buffer));
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