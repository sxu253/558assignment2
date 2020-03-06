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
 * Instantiates a TCP server and creates threads to process client requests.
 * This class creates the key-value storage that all client threads manipulate.
 */
public class Server {

	// Variables
//	String value;
//	String key;
//	Registry registry;
	private static int port = 9090;
	private static String disconnect = "open";
	private static String[] members;

	public static void runTcpProtocolServer(String args[]) {
		port = Integer.valueOf(args[1]);
		KeyValueStore kvStore = new KeyValueStore();
		try {
			ServerSocket serv = new ServerSocket(port);
			while(!disconnect.equalsIgnoreCase("exit")){
				Socket socket = serv.accept();
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String clientInput = input.readLine();
				String[] message = clientInput.split(" ");
				if(message[3].equalsIgnoreCase("exit")) {
					disconnect = "exit";
				} else if (args[0].equalsIgnoreCase("mem")) {
					clientInput.replace("mem ", "");
					members = clientInput.split(" ");
				} else {
					ThreadHandler thread = new ThreadHandler(socket, message, kvStore);
					thread.start();
				}
			}
			serv.close();
		} catch(IOException e) {
			System.out.println("Port not available.");
		}
	}

	// Implement server side socket for UDP
	public void runUdpProtocolServer(int port) throws IOException {
		// Created socket to listen at specified port
		try (DatagramSocket ds = new DatagramSocket(port)) {
			System.out.println("Server is listening on port " + port);
			byte[] receive = new byte[65535];
			DatagramPacket DpReceive = null;
			while (true) {
				DpReceive = new DatagramPacket(receive, receive.length);
				ds.receive(DpReceive);
				System.out.println("Starting a new thread for this client");
				//ClientThreadHandlerUDP thu = new ClientThreadHandlerUDP(ds, storeMap, DpReceive, receive, "hello");
				//Thread t = new Thread(thu);
				//t.start();
			}
		}
	}
}
