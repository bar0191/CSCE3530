/* 
  Brandon Reid
  CSCE3530
  Program 3 - DCHP UDP Server

  Compilation: javac UDPServer.java
  Execution  : java Server <port_number> [eg. port_number = 5000, where port_number is the UDP server port number]
*/

import java.io.*;
import java.net.*;
import java.util.*;

class UDPServer
{
  @SuppressWarnings("unchecked") // needed for (Map<>) obj;
	public static void main(String args[]) throws Exception
 	{
    // initalize UDP server socket, port, and byte streams
  	int sportno = Integer.parseInt(args[0]); /*UDP server port number */
    DatagramSocket serverSocket = new DatagramSocket(sportno);
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
  	System.out.println("UDP server is listening to the port number " + sportno + "...");
    
    // continous while loop for multiple packets
    while(true)
    {
  		/* Waiting for client's DCHP discovery packet */
      // initialize streams and recieve UDP packet
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      byte[] clientMsg = receivePacket.getData();

      // initialize bytearray for bytes to Map conversion
      ByteArrayInputStream byteIn = new ByteArrayInputStream(clientMsg);
      ObjectInputStream in = new ObjectInputStream(byteIn);

      // initialize object to map
      Map<String, String> request = (Map<String, String>) in.readObject();

      // if initial yiaddr is not 0.0.0.0 then its a DCHP Request
      if(!checkMap(request).equals("0.0.0.0")) {
        // output request from client to screen
        System.out.println("\n DHCP Request ");
        System.out.println("-----------------");
        printMap(request);

        // output ACK to screen
        System.out.println("\n DHCP ACK ");
        System.out.println("---------------");
        printMap(request);

        // initalize bytearray stream from map to byte conversion
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(request);

        /* Getting the IP address and port number of client */
        InetAddress IPAddress = receivePacket.getAddress(); /*UDP client IP address */
        int cportno = receivePacket.getPort(); /*UDP client port number */

        // send ACK packet to client
        sendData = byteOut.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, cportno);
        serverSocket.send(sendPacket); 

        // scan for IPaddress.txt and initalize ArrayList of IPs
        Scanner fileScan = new Scanner(new File("IPaddress.txt"));
        ArrayList<String> list = new ArrayList<String>();

        // add each IP in .txt to an arraylist
        while (fileScan.hasNextLine()) {
          list.add(fileScan.nextLine());
        }

        // remove IP reserved to client (top of stack)
        list.remove(0);

        // initalize filewriter
        FileWriter fileOutput = new FileWriter("IPaddress.txt");

        // write new arraylist of IPs to .txt
        for (String str: list) {
          fileOutput.write(str + "\n");
        }
        fileOutput.close();

      } else {
        System.out.println("\nDCHP Packet recieved from Client....  ");
        System.out.println("\n DHCP Discovery ");
        System.out.println("-----------------");
        printMap(request);

        // send offer message
        Scanner fileScan = new Scanner(new File("IPaddress.txt"));
        ArrayList<String> list = new ArrayList<String>();

        // add each IP in .txt to an arraylist
        while (fileScan.hasNextLine()) {
          list.add(fileScan.nextLine());
        }

        // add IP and Lifetime to Map
        request.put("yiaddr", list.get(0));
        request.put("Lifetime", "3600 secs");

        // output Offer packet to screen
        System.out.println("\n DHCP Offer ");
        System.out.println("---------------");
        printMap(request);

        // initialize bytearray stream for map to byte conversion
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(request);

        /* Getting the IP address and port number of client */
        InetAddress IPAddress = receivePacket.getAddress(); /*UDP client IP address */
        int cportno = receivePacket.getPort(); /*UDP client port number */

        // send data to client
        sendData = byteOut.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, cportno);
        serverSocket.send(sendPacket);
      }
    }
  }

  // check Map to see if 0.0.0.0 or other
  public static String checkMap(Map<String, String> mp) {
    Iterator it = mp.entrySet().iterator();
    
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      if (pair.getValue().equals("0.0.0.0")) {
        return pair.getValue().toString();
      } else {
        return pair.getValue().toString();
      }
    }
    return null;
  }

  // print Map function
  public static void printMap(Map<String, String> mp) {
    Iterator iter = mp.entrySet().iterator();
    
    while (iter.hasNext()) {
      Map.Entry pair = (Map.Entry)iter.next();
      System.out.println(pair.getKey() + ": " + pair.getValue());
    }
    System.out.println();
  }
}