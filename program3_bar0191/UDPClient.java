/* 
   Brandon Reid
   CSCE3530
   Program 3 - DCHP UDP Client
   
   Compilation: javac UDPClient.java
   Execution  : java Client <port_number> [eg. port_number = 5000, where port_number is the UDP server port number]
*/

import java.io.*;
import java.net.*;
import java.util.*;

class UDPClient
{
   @SuppressWarnings("unchecked") // needed for (Map<>) obj;
	public static void main(String args[]) throws Exception
   {

      // initialize bufferreader, portno, UDPSocket, and Server Host
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int sportno = Integer.parseInt(args[0]); /* UDP server port number */
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("cse01.cse.unt.edu"); /* UDP server's IP address */
      
      // initialize RandomNum generator for 100-999
      Random random = new Random();
      int transactionID = random.nextInt(900) + 100;

      // initialize DHCP Discovery, use HashMap for key-value store
      Map<String, String> data = new HashMap<String, String>();
      data.put("yiaddr", "0.0.0.0");
      data.put("Transaction ID", Integer.toString(transactionID));
      System.out.println("\n DHCP Discovery ");
      System.out.println("----------------");
      printMap(data); // print Packet Payload data to screen

      // create ByteArray used for converting Map to Bytes
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(byteOut);
      out.writeObject(data);

      // initialize byte streams
		byte[] sendData = byteOut.toByteArray();
      byte[] receiveData = new byte[1024];

      // Create UDP Packet and send to server
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, sportno);
      clientSocket.send(sendPacket);

		/* Receiving offer reply from the UDP server */
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      byte[] clientMsg = receivePacket.getData(); // store reply as Bytes
      
      // create new ByteArray Stream to convert recieved bytes to Map
      ByteArrayInputStream byteIn = new ByteArrayInputStream(clientMsg);
      ObjectInputStream in = new ObjectInputStream(byteIn);

      // initialize response Map
      Map<String, String> response = (Map<String, String>) in.readObject();
     
      // print DHCP offer Data from server
      System.out.println("\n DHCP Offer ");
      System.out.println("-------------");
      printMap(response);
      /****************************************************/
      // Create DHCP request

      // increment transaction ID for request
      int newTID = Integer.parseInt(response.get("Transaction ID")) + 1;

      // update data Map for request payload
      response.put("Transaction ID", Integer.toString(newTID));
      System.out.println("\n DHCP Request ");
      System.out.println("----------------");
      printMap(response); // print DCHP request to screen

      // Create new ByteArray stream to convert map to byte
      ByteArrayOutputStream requestByteOut = new ByteArrayOutputStream();
      ObjectOutputStream requestOut = new ObjectOutputStream(requestByteOut);
      requestOut.writeObject(response);

      // initialize byte data and send UDP packet to server
      byte[] sendRequestData = requestByteOut.toByteArray();
      DatagramPacket sendRequestPacket = new DatagramPacket(sendRequestData, sendRequestData.length, IPAddress, sportno);
      clientSocket.send(sendRequestPacket);
      /****************************************************/
      // recieve DCHP ACK 

      // initialize new byte stream for ACK data
      byte[] receiveACKData = new byte[1024];

      /* Receiving ACK reply from the UDP server */
      DatagramPacket receiveACKPacket = new DatagramPacket(receiveACKData, receiveACKData.length);
      clientSocket.receive(receiveACKPacket);
      byte[] clientACKMsg = receiveACKPacket.getData();
      
      // create new bytearray stream to convert byte stream to Map
      ByteArrayInputStream byteInACK = new ByteArrayInputStream(clientACKMsg);
      ObjectInputStream inACK = new ObjectInputStream(byteInACK);

      // initialze Map
      Map<String, String> responseACK = (Map<String, String>) inACK.readObject();
     
      System.out.println("\n DHCP ACK ");
      System.out.println("-------------");
      printMap(responseACK); // display DCHP Ack data
      /****************************************************/

      clientSocket.close(); // close UDP socket
   }

   // print Map function for DCHP payload data
   public static void printMap(Map<String, String> mp) {
    Iterator iter = mp.entrySet().iterator();
    
    while (iter.hasNext()) {
      Map.Entry pair = (Map.Entry)iter.next();
      System.out.println(pair.getKey() + ": " + pair.getValue());
    }
    System.out.println();
  }
}