import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The thread class that processes separate client commands for the UDP and TCP protocol severs.
 */
public class ThreadHandler extends Thread {
    private Socket tcpSocket = null;
    private DatagramSocket udpSocket = null;
    private KeyValueStore kvStore;
    private String[] args;
    private String protocolType;
    private DatagramPacket inPacket = null;
    private String response = null;
	private String type = null;
	private ConcurrentHashMap<String, String> operations;

    /**
     * TCP constructor. This constructor requires parameters necessary for processing TCP requests.
     *
     * @param tcpSocket the TCP socket on which information is sent out.
     * @param args the arguments from the client's message.
     * @param kvStore the key-value store utilized by the TCP server.
     */
    public ThreadHandler(Socket tcpSocket, String args[], KeyValueStore kvStore, String type) {
        this.args = args;
        this.kvStore = kvStore;
        this.tcpSocket = tcpSocket;
        this.type = type;
    }
    
    public ThreadHandler(Socket tcpSocket, String args[], KeyValueStore kvStore, String type, ConcurrentHashMap<String, String> operations) {
        this.args = args;
        this.kvStore = kvStore;
        this.tcpSocket = tcpSocket;
        this.type = type;
        this.operations = operations;
    }

    /**
     * UDP constructor. This constructor requires parameters necessary for processing UDP requests.
     *
     * @param udpSocket the socket on which information is sent out.
     * @param args the arguments from the client's message.
     * @param kvStore the key-value store utilized by the TCP server.
     * @param inPacket the packet received from the client.
     */
//    public ClientThread(DatagramSocket udpSocket, String args[], KeyValueStore kvStore, DatagramPacket inPacket) {
//        this.args = args;
//        this.kvStore = kvStore;
//        this.udpSocket = udpSocket;
//        this.protocolType = "udp";
//        this.inPacket = inPacket;
//    }

    /**
     * The method that runs the thread. This method extract information from the client's request. Then it processes
     * it as a UDP or TCP request dependent on the protocol type.
     */
    public void run() {
        try {
        	PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
        	System.out.println(type.contains("client"));
        	System.out.println(type);
        	System.out.println(type.length());
        	if (type.contains("client")) {
        		
        		String key = null;
                String value = null;
                String command = null;
                command = args[3].trim();
                if (args.length > 5) {
                    key = args[4];
                    value = args[5];
                } else if (args.length > 4) {
                    key = args[4];
                }
                processTcpConnection(out, command, key, value);
        	} else if (type.equalsIgnoreCase("server")) {
        		processServerConnection(out);
        	} else {
        		processLeaderServerConnection(out);
        	}
            
//            if (protocolType.equalsIgnoreCase("tcp")) {
//                PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
//                processTcpConnection(out, command, key, value);
//            } else if (protocolType.equalsIgnoreCase("udp")) {
//                processUdpConnection(command, key, value);
//            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    private void processServerConnection(PrintWriter out) {
    	//handles if not abort 
    	if (args[0].equalsIgnoreCase("okay") || args[0].equalsIgnoreCase("update")) {
    		String key = null;
    		String value = null;
    		String command = null;
    		command = args[1];
    		if (args.length > 3) {
    			//put
    			key = args[2];
    			value = args[3];
    		} else {
    			//del
    			key = args[2];
    		}
    		processTcpConnection(out, command, key, value);
    	}
    }
    
    private void processLeaderServerConnection(PrintWriter out) {
    	System.out.println("printing from processLeaderServerConnection");
    }

    /**
     * This method process the command given by a UDP connection and sends out the message packet(s) to the client.
     *
     * @param command the command given by the client.
     * @param key the key if a key was associated with the command or null otherwise.
     * @param value the value if a value was associated with the command or null otherwise.
     */
    private void processUdpConnection(String command, String key, String value) {
        DatagramPacket outPacket = null;
        byte[] responseBytes = new byte[6400];
        if (command.contains("put")) {
            kvStore.putKeyValue(key.trim(), value.trim());
            String response = "server response:" + command + " key:" + key;
            responseBytes = response.getBytes();
            outPacket = new DatagramPacket(responseBytes, responseBytes.length, inPacket.getAddress(), inPacket.getPort());
        } else if (command.contains("get")) {
            String response = "server response:" + command + " key=" + key + " " + command + " val="
                    + kvStore.getValue(key.trim());
            responseBytes = response.getBytes();
            outPacket = new DatagramPacket(responseBytes, responseBytes.length, inPacket.getAddress(), inPacket.getPort());
        } else if (command.contains("del")) {
            kvStore.deleteKey(key.trim());
            String response = "server response:" + command + " key:" + key;
            responseBytes = response.getBytes();
            outPacket = new DatagramPacket(responseBytes, responseBytes.length, inPacket.getAddress(), inPacket.getPort());
        } else if (command.contains("store")) {
            String response = "sever response: " + kvStore.store();
            String [] responseLines = response.split("\n");
            responseBytes = (String.valueOf(responseLines.length)).getBytes();
            outPacket = new DatagramPacket(responseBytes, responseBytes.length, inPacket.getAddress(), inPacket.getPort());
            sendPacket(outPacket);
            for(int i = 0; i < responseLines.length; i++) {
                responseBytes = responseLines[i].getBytes();
                outPacket = new DatagramPacket(responseBytes, responseBytes.length, inPacket.getAddress(), inPacket.getPort());
                sendPacket(outPacket);
            }
        }
        if(!command.contains("store")){
            sendPacket(outPacket);
        }
    }

    /**
     * Sends a UDP packet out.
     *
     * @param outPacket the packet to be sent.
     */
    private void sendPacket(DatagramPacket outPacket) {
        try {
            udpSocket.send(outPacket);
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