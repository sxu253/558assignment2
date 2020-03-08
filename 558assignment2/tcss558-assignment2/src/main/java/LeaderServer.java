/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
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
    //    private static ArrayList<String> members = new ArrayList<String>();
    private static Integer port;
    private static Socket serveSocket;
    private static KeyValueStore kvStore;

    public static void runTcpProtocolServer(String args[]) {
        port = Integer.valueOf(args[1]);
        kvStore = new KeyValueStore();
        readFile();
        MembershipThread  memberThread = new MembershipThread(members, kvStore ,operations, port);
        memberThread.start();


        try {
            ServerSocket leaderServerSocket = new ServerSocket(port);
            clientCommunication(leaderServerSocket, kvStore);
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void readFile() {
        File file = new File("nodes.txt");
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                String[] info = (sc.nextLine().split(":"));
                InetAddress ip = InetAddress.getByName(info[0]);
                int port = Integer.valueOf(info[1]);
                if (!members.containsKey(port)) {
                    members.put(port, ip);
                }
                //sb.append(sc.nextLine());
                //sb.append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void clientCommunication(ServerSocket socket, KeyValueStore kvStore) {
//        try {
//            while(!disconnect.equalsIgnoreCase("exit")){
        serverCommunication();
//                System.out.println(members.toString());
//                Socket clientSocket = socket.accept();
//                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                String clientInput = input.readLine();
//                String[] message = clientInput.split(" ");
//                if(disconnect.equalsIgnoreCase("exit")) {
//                    disconnect = "exit";
//                } else {
        ThreadHandler clientThread = new ThreadHandler(socket, kvStore, "client");
        clientThread.start();
//                }
//            }
//            socket.close();
//        } catch(IOException e) {
//            System.out.println("Port not available.");
//        }
    }

    public static void serverCommunication() {
        System.out.println(members.toString());
        for (Iterator<Entry<Integer, InetAddress>> iterator = members.entrySet().iterator(); iterator.hasNext();) {
            Entry<Integer, InetAddress> mapElement = iterator.next();
            System.out.println("inside for loop");
            InetAddress ip = (InetAddress) mapElement.getValue();
            int port = (int) mapElement.getKey();
//            System.out.println(port);
            try {
                serveSocket = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.out.println("opening sockets " + port);
//            PrintWriter out = new PrintWriter(serveSocket.getOutputStream(), true);
//            out.println("hello from leader!!" + port);
//            BufferedReader input = new BufferedReader(new InputStreamReader(serveSocket.getInputStream()));
//            String clientInput = input.readLine();
//            System.out.println(clientInput);
//            String[] serverMessage = clientInput.split(" ");

            ThreadHandler serverThread = new ThreadHandler(serveSocket, kvStore, "lserver", operations);
            serverThread.start();
//            serveSocket.close();

        }
    }
}
