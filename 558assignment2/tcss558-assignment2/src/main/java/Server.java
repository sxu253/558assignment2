/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

	// Variables
	HashMap<String, String> storeMap = new HashMap<>();
	HashMap<String, String> storeMapRmi = new HashMap<>();
	String value;
	String key;
	Registry registry;

	// Implement server side socket for TCP
	public void runTcpProtocolServer(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server is listening on port " + port);

			while (true) {
				System.out.println("Starting a new thread for this client");
				ClientThreadHandlerTCP th = new ClientThreadHandlerTCP(serverSocket.accept(), storeMap);
				Thread t = new Thread(th);
				t.start();
			}
		} catch (IOException e) {
			System.out.println("Accept failed");
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
				ClientThreadHandlerUDP thu = new ClientThreadHandlerUDP(ds, storeMap, DpReceive, receive);
				Thread t = new Thread(thu);
				t.start();
			}
		}
	}
}
