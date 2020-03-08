
/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class ThreadHandlerTCP implements Runnable {

	private Socket clientSocket;
	private StoreImplementation store;
	String value;
	String key;
	String type;

	ThreadHandlerTCP(Socket clientSocket, StoreImplementation store, String type) {
		this.type = type;
		this.clientSocket = clientSocket;
		this.store = store;
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
			if(type.equalsIgnoreCase("leader")){
				implementLeaderServerOperations(clientSocket, writer, task);
			} else if(type.equalsIgnoreCase("client")){
				implementClientOperations(clientSocket, writer, task);
			} else {
				implementServerOperations(clientSocket, writer, task);
			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// implements conditions for different operations
	private void implementClientOperations(Socket socket, PrintWriter writer, String task) throws IOException {

		System.out.println(task);
		if (task != null) {
			String[] taskKeyValue = task.split(" ");
			System.out.println(" Values by client: " + (Arrays.toString(taskKeyValue)));

			// PUT implementation
			if (taskKeyValue[0] != null && taskKeyValue[0].equalsIgnoreCase("put")) {
				System.out.println(store.putValuesInStrore(taskKeyValue));
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
				System.out.println(store.deleteValuesFromStore(taskKeyValue));
			}

			// Print store information
			else if (taskKeyValue[0].equalsIgnoreCase("store")) {
				writer.println(store);
			}

			// Close the socket
			else if (taskKeyValue[0].equalsIgnoreCase("exit")) {
				socket.close();
			}
		}
	}

	private void implementServerOperations(Socket socket, PrintWriter writer, String task) throws IOException {
		String[] taskKeyValue;
		if (task != null) {
			if(task.contains("mem"))
				task.replace("mem ", "");
			taskKeyValue = task.split(" ");

			System.out.println(" Values by client: " + (Arrays.toString(taskKeyValue)));
		}
	}

	private void implementLeaderServerOperations(Socket socket, PrintWriter writer, String task) throws IOException {
			if (task != null) {
				String[] taskKeyValue = task.split(" ");
				System.out.println(" Values by client: " + (Arrays.toString(taskKeyValue)));
		}
	}

}
