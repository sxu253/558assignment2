
/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 *This program takes in CLI arguments, and establishes TCP, UDP, or RMI connections.
 */
import java.io.IOException;
import java.net.UnknownHostException;

public class GenericNode {

	public static void main(String[] args) throws UnknownHostException, IOException {

		// Declare and initialize variables
		int port;
		String protocol = args[0];
		String hostName = null;
		String task = null;
		String key = null;
		String value = null;

		// Determines which server type is to be run
		if (protocol.equalsIgnoreCase("ts")) {
			
			port = Integer.parseInt(args[1]);
			if (port != 4410) {
				//Server server = new Server();
				Server.runTcpProtocolServer(args);
			} else {
				//LeaderServer leadServer = new LeaderServer();
				LeaderServer.runTcpProtocolServer(args);
			}
		}
		
		if (protocol.equals("tc")) {
			// Determines which client type is to be run
			hostName = args[1];
			port = Integer.parseInt(args[2]);
			if (args.length > 3) {
				task = args[3];
			}
			if (args.length > 4) {
				key = args[4];
			}
			if (args.length > 5) {
				value = args[5];
			}
			Client client = new Client();
			client.runTcpProtocolClient(hostName, port, task, key, value);
		}
	}
}