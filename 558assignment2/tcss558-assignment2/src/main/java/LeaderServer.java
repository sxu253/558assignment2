/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1
 */

import java.io.*;
import java.net.*;
import java.util.*;
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
    static ConcurrentHashMap<Integer, InetAddress> members = new ConcurrentHashMap<>();
    private static Integer port;
    private static Socket serveSocket;
    private static KeyValueStore kvStore;

    public static void runTcpProtocolServer(String args[]) {
        port = Integer.valueOf(args[1]);
        kvStore = new KeyValueStore();
        readFile();
        members.remove(port);
//        MembershipThread memberThread = new MembershipThread(members, kvStore, operations, port);
//        memberThread.start();
        try {
            ServerSocket leaderServerSocket = new ServerSocket(port);
            ServerSocket clientSocket = new ServerSocket(port + 10);
            clientCommunication(leaderServerSocket, clientSocket, kvStore);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void readFile() {
        File file = new File("nodes.txt");
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String[] info = (sc.nextLine().split(":"));
                InetAddress ip = InetAddress.getByName(info[0]);
                int memberPort = Integer.valueOf(info[1]);
                if (!members.containsKey(memberPort)) {
                    members.put(memberPort, ip);
                }
            }
            System.out.println(members.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void clientCommunication(ServerSocket socket, ServerSocket clientSocket, KeyValueStore kvStore) {
        ThreadHandler clientThread = new ThreadHandler(socket, clientSocket, kvStore, "client", members, port);
        clientThread.start();
        serverCommunication();
    }

    public static void serverCommunication() {
        System.out.println("sever comm. method in leader server begins");
        System.out.println("members:"+members.toString());
        for (Iterator<Map.Entry<Integer, InetAddress>> iterator = members.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, InetAddress> mapElement = iterator.next();
            InetAddress ip = (InetAddress) mapElement.getValue();
            int memberPort = (int) mapElement.getKey();
            System.out.println("Thread spawn for: "+memberPort+" listening");
            ServerCommunicationThread thread = new ServerCommunicationThread(ip, memberPort, "casting votes");
            thread.start();
        }
    }
}
