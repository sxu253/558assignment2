import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class instantiates a TCP client that sends commands to a TCP server.
 */
public class Client {
	/**
	 * This method is called by GenericNode to start a TCP client with connection to a specified TCP server.
	 * @param args the command line arguments.
	 */
	public static void start(String args[]) {
		String ipAddr = args[1];
		int port = Integer.valueOf(args[2]);
		String cmd = args[3];
		String disconnect = "open";
		String message = String.join(" ", args);
		try {
			InetAddress serverAddr = InetAddress.getByName(ipAddr);
			Socket socket = new Socket(serverAddr, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(message);
			System.out.println("Client messsage sent: "+message);
			String response = null;
			StringBuilder sb = new StringBuilder();
			response = input.readLine();
			System.out.println(response);
			input.close();
			out.close();
			socket.close();
		} catch(UnknownHostException ue) {
			System.out.println(ue.toString());
		} catch(IOException ie) {
			System.out.println(ie.toString());
		} catch(IllegalArgumentException ae) {
			System.out.println(ae.toString());
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
}