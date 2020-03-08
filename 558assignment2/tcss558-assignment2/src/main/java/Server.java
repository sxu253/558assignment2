/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1
 */

import java.io.*;
import java.net.*;
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
			ServerSocket leaderServerSocket = new ServerSocket(port);
			int clientport = port + 10;
			System.out.println(clientport);
			ServerSocket clientSocket = new ServerSocket(clientport);

			//LEADER SERVER COMMUNICATION
			leaderServerCommunication(leaderServerSocket, kvStore);

			clientCommunication(clientSocket, kvStore);


		} catch (IOException e) {
			System.out.println("Port not available.");
		}
	}

	public static void leaderServerCommunication(ServerSocket leaderServerSocket, KeyValueStore kvStore) {
			ThreadHandler thread = new ThreadHandler(leaderServerSocket, kvStore, "server");
			thread.start();


	}

	public static void clientCommunication(ServerSocket clientSocket, KeyValueStore kvStore) {
		ThreadHandler clientThread = new ThreadHandler(clientSocket, kvStore, "client");
		clientThread.start();
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
