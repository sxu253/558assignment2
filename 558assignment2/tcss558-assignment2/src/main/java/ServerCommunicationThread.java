import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCommunicationThread extends Thread {
    private static ConcurrentHashMap<Integer, InetAddress> members;
    private static int serverPort;
    private static Socket serverSocket;
    private static InetAddress ip;
    private static volatile int memberPort;
    private static String clientInput;
    private static String type;
    private static ServerSocket socket;
    private static ConcurrentHashMap<Integer, String> replies;
    public ServerCommunicationThread(InetAddress ip, int memberPort, String type) {
//        this.members = members;
//        this.serverPort = serverPort;
        this.ip = ip;
        this.memberPort = memberPort;
        this.type = type;
    }

    public ServerCommunicationThread(ServerSocket socket, String clientInput, InetAddress ip, int memberPort, String type,
                                     ConcurrentHashMap<Integer,String> replies) {
//        this.members = members;
//        this.serverPort = serverPort;
        this.clientInput = clientInput;
        this.ip = ip;
        this.memberPort = memberPort;
        this.type = type;
        this.replies = replies;
        this.socket = socket;
    }

    public void run() {
        if(type.equalsIgnoreCase("casting votes"))
            serverCommunication();
        if(type.equalsIgnoreCase("collect votes"))
            forwardCommand();
    }

    private void forwardCommand() {
        System.out.println("forward command");
        String serverInput = null;
        BufferedReader input;
        try {
            Socket servSocket = socket.accept();
            PrintWriter out = new PrintWriter(servSocket.getOutputStream(), true);
            out.println(clientInput);
            input = new BufferedReader(new InputStreamReader(servSocket.getInputStream()));
            serverInput = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if(replies.containsKey(memberPort)) {
//            replies.put((memberPort + 1), serverInput);
//        } else {
        replies.put(memberPort, serverInput);
//        }
        System.out.println("port used as key in replies" + memberPort);
        System.out.println("server input in forward command: " + serverInput);
        //add checking for okay and do storage changes
        String[] serverMessage = serverInput.split(" ");
        System.out.println("printing from end of forwardcommand");
    }

    public static void serverCommunication() {
        System.out.println("polling for info from: "+memberPort);
        //THIS IS WHERE THINGS GO WRONG
        setupConnection();
        System.out.println("passed connection on port: "+memberPort);
        String serverInput = null;
        String disconnect = "open";
        BufferedReader input;
        while(!disconnect.equalsIgnoreCase("exit"))
        {
            try {
                PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                serverInput = input.readLine();
                System.out.println(serverInput);
                out.println("abort");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setupConnection() {
        Boolean attemptConnect = true;
        while(attemptConnect) {
            try {
                System.out.println("port inside set up connection: " + memberPort);
                serverSocket = new Socket(ip, memberPort);
                attemptConnect = false;
            } catch (IOException e) {
                System.out.println("port is unavailable");
                attemptConnect = true;
            }
        }
    }
}
