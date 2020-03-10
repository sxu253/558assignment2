import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The thread class that processes separate client commands for the UDP and TCP protocol severs.
 */
public class ThreadHandler extends Thread {
    private ServerSocket tcpSocket = null;
    private ServerSocket serverSocket = null;
    private DatagramSocket udpSocket = null;
    private KeyValueStore kvStore;
    private String[] args;
    private String protocolType;
    private DatagramPacket inPacket = null;
    private String response = null;
    private String type = null;
    private ConcurrentHashMap<String, String> operations;
    private ConcurrentHashMap<Integer, String> replies;
    private ConcurrentHashMap<Integer, InetAddress> members;
    private ServerSocket clientSocket;
    private static int serverPort;

    /**
     * Constructor for use by servers handling client requests
     *
     * @param tcpSocket the TCP socket on which information is sent out.
     * @param kvStore the key-value store utilized by the TCP server.
     */
    public ThreadHandler(ServerSocket tcpSocket, ServerSocket clientSocket, KeyValueStore kvStore, String type,
                         ConcurrentHashMap<Integer, InetAddress> members, int serverPort) {
        this.args = args;
        this.kvStore = kvStore;
        this.tcpSocket = tcpSocket;
        this.type = type;
        this.members = members;
        this.clientSocket = clientSocket;
        this.serverPort = serverPort;
        replies = new ConcurrentHashMap<>();
    }

    /**
     * The method that runs the thread. This method extract information from the client's request. Then it processes
     * it as a UDP or TCP request dependent on the protocol type.
     */
    public void run() {
        clientCommunication();
//        if (type.contains("client")) {
//            clientCommunication();
//            String key = null;
//            String value = null;
//            String command = null;
//            command = args[3].trim();
//            if (args.length > 5) {
//                key = args[4];
//                value = args[5];
//            } else if (args.length > 4) {
//                key = args[4];
//            }
//            //editStorage(out, command, key, value);
//        } else if (type.equalsIgnoreCase("server")) {
//            serverToLeaderCommunication();
//        } else {
//            leaderToServerCommunication();
//        }
    }

    /**
     * Method for servers to process client requests.
     */
    private void clientCommunication() {
        String disconnect = "open";
        Socket cSocket = null;
        String response = null;
        try {
            while (!disconnect.equalsIgnoreCase("exit")) {
                cSocket = clientSocket.accept();
                PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
                String clientInput = input.readLine();
                String[] message = clientInput.split(" ");
                System.out.println(clientInput);
                forwardCommand(clientInput);
//                out.println("Verifying request");
                //check replies here and do appropriate behavior
                while(replies.size() != members.size()){
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    System.out.println(replies.size());
                }
                System.out.println("What's in replies: "+replies.toString());
                if(replies.containsValue("abort")) {
                    System.out.println("return info to client");
                    out.println("abort");
                    replies.clear();
                }

//                out.println(response);
                if (disconnect.equalsIgnoreCase("exit")) {
                    disconnect = "exit";
                }
            }
            cSocket.close();
        } catch (IOException e) {
            System.out.println("Port not available.");
        }
    }

    private void forwardCommand(String clientInput) {
        for (Iterator<Map.Entry<Integer, InetAddress>> iterator = members.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, InetAddress> mapElement = iterator.next();
            InetAddress ip = (InetAddress) mapElement.getValue();
            int port = (int) mapElement.getKey();
//            if(serverPort != port){
                System.out.println("vote: "+port);
                ServerCommunicationThread thread = new ServerCommunicationThread(tcpSocket, clientInput, ip, port,
                        "collect votes", replies);
                thread.start();
//            }
        }
    }

    private Socket setupConnection(InetAddress ip, int port) {
        Socket serveSocket =  null;
        try {
            serveSocket = new Socket(ip, port);
//            System.out.print(port);
        } catch (IOException e) {
            try {
                sleep(2500);
            } catch (InterruptedException ex) {
                System.out.println("couldn't sleep :(");
            }
            System.out.println("Socket port is busy");
            setupConnection(ip, port);
        }
        return serveSocket;
    }

    /**
     * Method for regular servers to communicate with the leader server.
     *
     */
//    private void serverToLeaderCommunication() {
//        String disconnect = "open";
//        Socket servSocket = null;
//        try {
//            servSocket = serverSocket.accept();
//            PrintWriter out = new PrintWriter(servSocket.getOutputStream(), true);
//            BufferedReader input = new BufferedReader(new InputStreamReader(servSocket.getInputStream()));
//            String clientInput = input.readLine();
//            String[] message = clientInput.split(" ");
//            out.println("transaction stuff from reg server connection");
//            System.out.println(clientInput);
//        } catch (IOException e) {
//            System.out.println("Port not available.");
//        }
//    }
//
//    /**
//     *not currently in use
//     */
//    private void leaderToServerCommunication() {
//        String severInput = null;
//        BufferedReader input = null;
//        try {
//            Socket s = tcpSocket.accept();
//            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
//            out.println("hello from leader!!");
//            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
//            severInput = input.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(severInput);
//        String[] serverMessage = severInput.split(" ");
//        System.out.println("printing from leaderToServerCommunication");
//    }

    /**
     * This method process the command given by a TCP connection and sends out the message to the client.
     *
     * @param out the PrinterWriter object to send out the message.
     * @param command the command given by the client.
     * @param key the key if a key was associated with the command or null otherwise.
     * @param value the value if a value was associated with the command or null otherwise.
     */
    private void editStorage(PrintWriter out, String command, String key, String value) {
        if (command.contains("put")) {
            kvStore.putKeyValue(key, value);
            String response = "server response:" + command + " key:" + key;
            out.println(response);
        } else if (command.contains("get")) {
            out.println("server response:" + command + " key=" + key + " " + command +
                    " val=" + kvStore.getValue(key));
        } else if (command.contains("del")) {
            kvStore.deleteKey(key);
            out.println("server response:" + command + " key:" + key);
        } else if (command.contains("store")) {
            out.println("sever response: " + kvStore.store());
        }
        out.close();
    }
}