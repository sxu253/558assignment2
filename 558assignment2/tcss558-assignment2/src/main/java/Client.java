
/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	// Implement client side socket for TCP
	public void runTcpProtocolClient(String hostName, int port, String task, String key, String value) {

		try (Socket socket = new Socket(hostName, port)) {

			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);

			if (task != null) {
				writer.println(task + " " + key + " " + value);

				if (task.equalsIgnoreCase("get") || task.equalsIgnoreCase("store") || task.equalsIgnoreCase("exit")) {
					System.out.println(reader.readLine());
				}

			} else {
				System.out.println("uc/tc <address> <port> put <key> <msg> UDP/TCP CLIENT: Put an object into store\n"
						+ "uc/tc <address> <port> get <key> UDP/TCP CLIENT: Get an object from store by key\n"
						+ "uc/tc <address> <port> del <key> UDP/TCP CLIENT: Delete an object from store by key\n"
						+ "uc/tc <address> <port> store UDP/TCP CLIENT: Display object store\n"
						+ "uc/tc <address> <port> exit UDP/TCP CLIENT: Shutdown server ");
			}

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}

	}
}