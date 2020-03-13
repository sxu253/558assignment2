package com.assignment2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The thread class that processes separate client commands for the UDP and TCP protocol severs.
 */
public class ClientThread extends Thread {
    private Socket tcpSocket = null;
    private DatagramSocket udpSocket = null;
    private KeyValueStore kvStore;
    private String[] args;
    private String protocolType;
    private DatagramPacket inPacket = null;
    private String response = null;
    private ConcurrentHashMap<Integer, InetAddress> members;
    private ConcurrentHashMap<String, String> operations;

    /**
     * TCP constructor. This constructor requires parameters necessary for processing TCP requests.
     *
     * @param tcpSocket the TCP socket on which information is sent out.
     * @param args the arguments from the client's message.
     * @param kvStore the key-value store utilized by the TCP server.
     * @param members the other server nodes in the network stored as key-value pairs, where the port is the key andd
     *                the ip address is the value
     * @param operations the keys currently being operated on by other transactions in the local key value store
     */
    public ClientThread(Socket tcpSocket, String args[], KeyValueStore kvStore,
                        ConcurrentHashMap<Integer, InetAddress> members, ConcurrentHashMap<String, String> operations) {
        this.args = args;
        this.kvStore = kvStore;
        this.protocolType = "tcp";
        this.tcpSocket = tcpSocket;
        this.members = members;
        this.operations = operations;
    }

    /**
     * The method that runs the thread. This method extract information from the client's request. Then it processes
     * it as a UDP or TCP request dependent on the protocol type.
     */
    public void run() {
        try {
            String key = null;
            String value = null;
            String command = null;
            if (args.length > 5) {
                key = args[4];
                value = args[5];
                command = args[3];
                System.out.println(command);
            } else if (args.length > 4) {
                key = args[4];
                command = args[3];
                System.out.println(command);

            } else if (args.length > 3) {
                command = args[3];
            }
            if (protocolType.equalsIgnoreCase("tcp")) {
                PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
                processTcpConnection(out, command, key, value);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * This method process the command given by a TCP connection and sends out the message to the client.
     *
     * @param out the PrinterWriter object to send out the message.
     * @param command the command given by the client.
     * @param key the key if a key was associated with the command or null otherwise.
     * @param value the value if a value was associated with the command or null otherwise.
     */
    private void processTcpConnection(PrintWriter out, String command, String key, String value) {
        //PUT
        System.out.println(command);
        if (command.equalsIgnoreCase("put")) {
            operations.put(key, value);
            ArrayList<String> replies = serverCommunication(key, value, "dput1");
            if(replies.contains("dputabort")) {
                out.println("Transaction was aborted.");
                operations.remove(key);
            } else {
                kvStore.putKeyValue(key, value);
                String response = "server response:" + command + " key:" + key;
                serverCommunication(key, value, "dput2");
                operations.remove(key);
                out.println(response);
            }
            //GET
        } else if (command.equalsIgnoreCase("get")) {
            out.println("server response:" + command + " key=" + key + " " + command +
                    " val=" + kvStore.getValue(key));
            //DEL
            //If we receive a del, we forward a ddel1 to all members, check for a ddelabort and act appropriately
        } else if (command.equalsIgnoreCase("del")) {
            operations.put(key, "lock");
            ArrayList<String> replies = serverCommunication(key, value, "ddel1");
            if(replies.contains("ddelabort")) {
                out.println("Transaction was aborted.");
                operations.remove(key);
            } else {
                kvStore.deleteKey(key);
                serverCommunication(key, value, "ddel2");
                operations.remove(key);
                out.println("server response:" + command + " key:" + key);
            }
            //STORE
        } else if (command.equalsIgnoreCase("store")) {
            out.println("sever response: " + kvStore.store());
            //DPUT1
        } else if (command.equalsIgnoreCase("dput1")) {
            if(operations.containsKey(key)) {
                out.println("dputabort");
            } else {
                out.println("okay");
            }
            //DPUT2
        } else if (command.equalsIgnoreCase("dput2")) {
            operations.put(key, value);
            kvStore.putKeyValue(key, value);
            out.println("put " + key + " " + value);
            operations.remove(key);
            //DDEL1
        } else if (command.equalsIgnoreCase("ddel1")) {
            if(operations.containsKey(key)) {
                out.println("ddelabort");
            } else {
                out.println("okay");
            }
        } else if (command.equalsIgnoreCase("ddel2")) {
            operations.put(key, "lock");
            kvStore.deleteKey(key);
            out.println("del " + key + " " + value);
            operations.remove(key);
        }
        out.close();
    }

    /**
     * Server communication allows to send dput1/ddel1 and dput2/ddel2 to the other server nodes in the network.
     * This method also receives the voting responses from other servers in the network.
     *
     * @param key the key to be edited the key value store
     * @param value the value to be stored if the client command was 'put', otherwise null is passed
     * @param command the command to be sent to the other network servers (dput1/dput2/ddel1/ddel2)
     * @return returns an ArrayList containing the responses of the network servers
     */
    private ArrayList<String> serverCommunication(String key, String value, String command) {
        System.out.println("Inside server communication: "+command);
        ArrayList<String> replies = new ArrayList<>();
        for (Iterator<Map.Entry<Integer, InetAddress>> iterator = members.entrySet().iterator(); iterator.hasNext(); ) {
            PrintWriter out = null;
            BufferedReader input = null;
            Map.Entry<Integer, InetAddress> mapElement = iterator.next();
            InetAddress ip = (InetAddress) mapElement.getValue();
            int memberPort = (int) mapElement.getKey();
            Boolean attemptConnection = true;
            Socket serverSocket = null;
            while(attemptConnection) {
                try {
                    serverSocket = new Socket(ip, memberPort);
                    attemptConnection = false;
                } catch (IOException e) {
                    attemptConnection = true;
                    System.out.println("Port is busy.");
                }
            }
            try {
                out = new PrintWriter(serverSocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                System.out.println("Inside try block: "+command + " " + key + " " + value);
                out.println("tc hostip port " + command + " " + key + " " + value);
                String response = input.readLine();
                replies.add(response);
                out.close();
                input.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return replies;
    }
}
