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
    private static int port = 4410;
    private static String disconnect = "open";
    ConcurrentHashMap<String, String> operations = new ConcurrentHashMap<>();
    private static String[] members;

    public static void runTcpProtocolServer(String args[]) {
//        port = Integer.valueOf(args[1]);
        KeyValueStore kvStore = new KeyValueStore();
        MembershipThread  memberThread = new MembershipThread(members);
        memberThread.start();
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
}
