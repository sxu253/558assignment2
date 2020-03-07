/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.registry.Registry;

/**
 * @author Danielle Lambion
 *
 *         Instantiates a TCP server and creates threads to process client
 *         requests. This class creates the key-value storage that all client
 *         threads manipulate.
 */
public class Server {

	// Variables
//	String value;
//	String key;
//	Registry registry;
	private static int port = 9090;
	private static int leaderPort = 4410;
	private static String disconnect = "open";
	private static String[] members;

	public static void runTcpProtocolServer(String args[]) {
		port = Integer.valueOf(args[1]);
		KeyValueStore kvStore = new KeyValueStore();
		try {
			// ServerSocket clientServerSocket = new ServerSocket(port);
			ServerSocket leaderServerSocket = new ServerSocket(port);
			
			int clientport = port + 10;
			
			ServerSocket clientSocket = new ServerSocket(clientport);

			while (!disconnect.equalsIgnoreCase("exit")) {
				//LEADER SERVER COMMUNICATION
				Socket leaderSocket = leaderServerSocket.accept();
				leaderServerCommunication(leaderSocket, kvStore);
				
				//CLIENT COMMUNICATION 
				Socket client = clientSocket.accept();
				String[] message = clientCommunication(client, kvStore);
				disconnect = message[3];
				if(!disconnect.equalsIgnoreCase("exit")) {
        			ThreadHandler thread = new ThreadHandler(client, message, kvStore, "client");
        			thread.start();
        		} 

			}
			clientSocket.close();
			leaderServerSocket.close();


		} catch (IOException e) {
			System.out.println("Port not available.");
		}
	}

	public static void leaderServerCommunication(Socket leaderServerSocket, KeyValueStore kvStore) {
		try {
			
			BufferedReader input = new BufferedReader(new InputStreamReader(leaderServerSocket.getInputStream()));
			String leaderInput = input.readLine();
			System.out.println(leaderInput);
			String[] message = leaderInput.split(" ");
			PrintWriter out = new PrintWriter(leaderServerSocket.getOutputStream(), true);
			out.println("hello from leaderServerCommunication" + port);
			if (message[0].equalsIgnoreCase("mem")) {
				leaderInput.replace("mem ", "");
				// members contains active membership
				members = leaderInput.split("\n");
			}
//			ThreadHandler thread = new ThreadHandler(leaderServerSocket, message, kvStore, "server");
//			thread.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String[] clientCommunication(Socket clientSocket, KeyValueStore kvStore) {
		String[] message = null;
		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String clientInput = input.readLine();
			System.out.println(clientInput);
			message = clientInput.split(" ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
		

	}

//	// Implement server side socket for UDP
//	public void runUdpProtocolServer(int port) throws IOException {
//		// Created socket to listen at specified port
//		try (DatagramSocket ds = new DatagramSocket(port)) {
//			System.out.println("Server is listening on port " + port);
//			byte[] receive = new byte[65535];
//			DatagramPacket DpReceive = null;
//			while (true) {
//				DpReceive = new DatagramPacket(receive, receive.length);
//				ds.receive(DpReceive);
//				System.out.println("Starting a new thread for this client");
//				//ClientThreadHandlerUDP thu = new ClientThreadHandlerUDP(ds, storeMap, DpReceive, receive, "hello");
//				//Thread t = new Thread(thu);
//				//t.start();
//			}
//		}
//	}
}
