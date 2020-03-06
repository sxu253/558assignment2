/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class ClientThreadHandlerUDP implements Runnable {

	private DatagramSocket ds;
	private HashMap<String, String> storeMap = new HashMap<>();
	String value;
	String key;
	DatagramPacket DpReceive;
	byte[] receive;

	ClientThreadHandlerUDP(DatagramSocket ds, HashMap<String, String> storeMap, DatagramPacket DpReceive,
			byte[] receive) {

		this.ds = ds;
		this.storeMap = storeMap;
		this.DpReceive = DpReceive;
		this.receive = receive;

	}

	public void run() {

		System.out.println("Inside Thread!!");

		// Print out data sent by client, received by server
		System.out.println("Client: " + data(receive));
		StringBuilder dataInput = data(receive);
		// Create string array that contains: task, key, value
		String[] taskKeyValue = dataInput.toString().split(" ");

		try {
			if (taskKeyValue[0] != null) {

				implementUdpOperations(taskKeyValue, ds);

			} else {
				System.out.println("uc/tc <address> <port> put <key> <msg> UDP/TCP CLIENT: Put an object into store\n"
						+ "uc/tc <address> <port> get <key> UDP/TCP CLIENT: Get an object from store by key\n"
						+ "uc/tc <address> <port> del <key> UDP/TCP CLIENT: Delete an object from store by key\n"
						+ "uc/tc <address> <port> store UDP/TCP CLIENT: Display object store\n"
						+ "uc/tc <address> <port> exit UDP/TCP CLIENT: Shutdown server ");
			}

		} catch (IOException e) {
			System.out.println("Accept failed");
		}
		receive = new byte[65535];
	}

	private void implementUdpOperations(String[] taskKeyValue, DatagramSocket ds) throws IOException {

		StoreImplementation store = new StoreImplementation(storeMap);

		// Put implementation
		if (taskKeyValue[0].equalsIgnoreCase("put") && taskKeyValue[0] != null) {
			storeMap = store.putValuesInStrore(taskKeyValue);
			System.out.println(storeMap);
		}

		// Get implementation
		else if (taskKeyValue[0].equalsIgnoreCase("get")) {
			value = store.getValuesFromStore(taskKeyValue);
			if (value != null) {
				System.out.println(value);
			} else {
				System.out.println("No such key exist in the store");
			}
		}

		// Delete implementation
		else if (taskKeyValue[0].equalsIgnoreCase("del")) {
			storeMap = store.deleteValuesFromStore(taskKeyValue);
			System.out.println(storeMap);
		}

		// Print store information
		else if (taskKeyValue[0].equalsIgnoreCase("store")) {
			System.out.println(storeMap);
		}

		// Close the socket
		else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
			try {
				System.out.println("Exit connection");
				ds.close();
			} catch (Exception e) {
			}
		}

	}

	// A utility method to convert the sent byte array data into a string
	// representation
	public static StringBuilder data(byte[] a) {
		if (a == null)
			return null;
		StringBuilder ret = new StringBuilder();
		int i = 0;
		while (a[i] != 0) {
			ret.append((char) a[i]);
			i++;
		}
		return ret;
	}

}
