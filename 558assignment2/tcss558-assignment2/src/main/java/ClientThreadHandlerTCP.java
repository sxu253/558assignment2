
/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class ClientThreadHandlerTCP implements Runnable {

	private Socket clientSocket;
	private HashMap<String, String> storeMap = new HashMap<>();
	String value;
	String key;

	ClientThreadHandlerTCP(Socket clientSocket, HashMap<String, String> storeMap) {

		this.clientSocket = clientSocket;
		this.storeMap = storeMap;

	}

	public void run() {

		BufferedReader reader = null;
		PrintWriter writer = null;

		try {

			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writer = new PrintWriter(clientSocket.getOutputStream(), true);

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();

		}

		try {

			// Read the task key and value in a string
			String task = reader.readLine();

			// call function to implement the operations
			implementOperations(clientSocket, writer, task);

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();

		}

	}

	// implements conditions for different operations
	private void implementOperations(Socket socket, PrintWriter writer, String task) throws IOException {

		StoreImplementation store = new StoreImplementation(storeMap);

		System.out.println(task);
		if (task != null) {
			String[] taskKeyValue = task.split(" ");
			System.out.println(" Values by client: " + (Arrays.toString(taskKeyValue)));

			// PUT implementation
			if (taskKeyValue[0] != null && taskKeyValue[0].equalsIgnoreCase("put")) {
				storeMap = store.putValuesInStrore(taskKeyValue);
				System.out.println(storeMap);
			}

			// GET implementation
			else if (taskKeyValue[0].equalsIgnoreCase("get")) {
				value = store.getValuesFromStore(taskKeyValue);
				if (value != null) {
					writer.println(value);
				} else {
					writer.println("No such key exist in the store");
				}
			}

			// Delete implementation
			else if (taskKeyValue[0].equalsIgnoreCase("del")) {
				storeMap = store.deleteValuesFromStore(taskKeyValue);
				System.out.println(storeMap);
			}

			// Print store information
			else if (taskKeyValue[0].equalsIgnoreCase("store")) {
				writer.println(storeMap);
			}

			// Close the socket
			else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
				socket.close();
			}
		}
	}

	protected void finalize() {

		try {
			System.out.println("Close!!!!");
			clientSocket.close();

		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

}
