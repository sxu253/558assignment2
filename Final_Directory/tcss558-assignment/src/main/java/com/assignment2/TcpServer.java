package com.assignment2;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Danielle Lambion
 *
 * Instantiates a TCP server and creates threads to process client requests.
 * This class creates the key-value storage that all client threads manipulate.
 */
public class TcpServer {
    private static int port = 9090;
    private static String disconnect = "open";
    static ConcurrentHashMap<String, String> operations = new ConcurrentHashMap<>();
    static ConcurrentHashMap<Integer, InetAddress> members = new ConcurrentHashMap<>();

    /**
     * The method used by GenericNode to start a TCP server.
     *
     * @param args the command line arguments.
     */
    public static void start(String args[]) {
        port = Integer.valueOf(args[1]);
        readFile();
        members.remove(port);
        MembershipThread memThread = new MembershipThread(members, port);
        memThread.start();
        KeyValueStore kvStore = new KeyValueStore();
        try {
            ServerSocket serv = new ServerSocket(port);
            while(!disconnect.equalsIgnoreCase("exit")){
                Socket socket = serv.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientInput = input.readLine();
                String[] message = clientInput.split(" ");
                if(message.length > 3)
                    disconnect = message[3];
                else
                    disconnect = message[0];
                if(disconnect.equalsIgnoreCase("exit")) {
                    disconnect = "exit";
                } else {
                    ClientThread clientThread = new ClientThread(socket, message, kvStore, members, operations);
                    clientThread.start();
                }
            }
            serv.close();
        } catch(IOException e) {
            System.out.println("Port not available");
        }
    }

    /**
     * Called to intialize the membership data structure once when the server boots.
     * Reads the membership file and stores the members in the ConcurrentHashMap data structure.
     */
    public static void readFile() {
        File file = new File("nodes.cfg");
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
}
