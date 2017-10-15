// Client
// Referenced from source: https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
// Brandon Reid - CSCE3530
// Bar0191
// Program 2 - this client is based from program 1

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

// main client class
public class Client {
	// main
	public static void main(String args[]) throws IOException {

		// check for proper number of args
		if (args.length < 2 || args.length > 2) {
			System.out.println("ERROR: Invalid arguments: java Client hostname port");
			System.exit(0);
		}

		// check for proper port number in range 1024-49151
		if (Integer.parseInt(args[1]) < 1024 || Integer.parseInt(args[1]) > 49151) {
            System.out.println("ERROR: Invalid port: 1024 - 49151");
            System.exit(0);
        }

        // initialize client connection variables, sock, IO, buffer, address
	    InetAddress    address = InetAddress.getByName(args[0]);
	    Socket         sock    = null;
	    String         buffer  = null;
	    BufferedReader reader  = null;
	    BufferedReader input   = null;
	    PrintWriter    output  = null;

	    // create socket, socket IO, and buffer
	    try {
	        sock   = new Socket(address, Integer.parseInt(args[1]));
	        reader = new BufferedReader(new InputStreamReader(System.in));
	        input  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	        output = new PrintWriter(sock.getOutputStream());
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.err.print("IO Exception");
	    }

	    // display client connection success, prompt for response
	    System.out.println("Connection established on -> " + address);
	    System.out.println("Enter URL (Enter 'Exit' to close connection):");

	    // initialize response
	    String response = null;

	    // main connection management loop for IO
	    try {
	        buffer = reader.readLine(); // set buffer to socket input
	        // while buffer is not bye, send buffer to socket output
	        while(buffer.toLowerCase().compareTo("exit") != 0) {
	            output.println(buffer);
	            output.flush();
	            response = input.readLine();
	            System.out.println("Server Response -> " + response);
	            buffer = reader.readLine();
	        }
	    } catch(IOException e) {
	        e.printStackTrace();
	    	System.out.println("Socket read Error");
	    }
	    // once bye has terminated connection, close socket, and socket IO
	    finally {
	        input.close();
	        output.close();
	        reader.close();
	        sock.close();
	        System.out.println("Connection Closed");
	    }
	}
}