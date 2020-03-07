/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Danielle Lambion
 *
 * Instantiates a TCP server and creates threads to process client requests.
 * This class creates the key-value storage that all client threads manipulate.
 */
public class LeaderServer {

    // Variables
//	String value;
//	String key;
//	Registry registry;
    //private static int port = 9090;
    private static int serverPort = 4410;
    private static String disconnect = "open";
    static ConcurrentHashMap<String, String> operations = new ConcurrentHashMap<>();
    private static ArrayList<String> members = new ArrayList<String>();

    public static void runTcpProtocolServer(String args[]) {
        int port = Integer.valueOf(args[1]);
        KeyValueStore kvStore = new KeyValueStore();
        try (Socket socket = new Socket("localhost", port)) {

			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);
			writer.println("helllllo");
			

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
//        MembershipThread  memberThread = new MembershipThread(members, kvStore ,operations, port);
//        memberThread.start();
        
//        System.out.println(members.size());
        
//        try {
//            ServerSocket clientSocket = new ServerSocket(port);
//            for(int i = 0; i < members.size()-1; i++) {
//            	System.out.println(members.get(i));
//            	
//            	String[] info = (members.get(i)).split(":");
//            	
//            	int ip = Integer.valueOf(info[0]);
//            	port = Integer.valueOf(info[1]);
//            	ServerSocket serverSocket = new ServerSocket(port);
//            	Socket serveSocket = serverSocket.accept();
//            	PrintWriter out = new PrintWriter(serveSocket.getOutputStream(), true);
//                BufferedReader input = new BufferedReader(new InputStreamReader(serveSocket.getInputStream()));
//                String clientInput = input.readLine();
//                String[] message = clientInput.split(" ");
//            	ThreadHandler thread = new ThreadHandler(serveSocket, message, kvStore, "leaderServer", operations);
//                thread.start();
//            }
//            while(!disconnect.equalsIgnoreCase("exit")){
//                Socket socket = clientSocket.accept();
//                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String clientInput = input.readLine();
//                String[] message = clientInput.split(" ");
//                if(message[3].equalsIgnoreCase("exit")) {
//                    disconnect = "exit";
//                } else {
//                    ThreadHandler thread = new ThreadHandler(socket, message, kvStore, "client", operations);
//                    thread.start();
//                }
//            }
//            clientSocket.close();
//        } catch(IOException e) {
//            System.out.println("Port not available.");
//        }
    }
    
    
}
