import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class MembershipThread extends Thread {
	ArrayList<String> members;
	private Scanner sc;
	private KeyValueStore kvStore;
	private ConcurrentHashMap<String, String> operations;
	private int port;

	public MembershipThread(ArrayList<String> members, KeyValueStore kvStore, ConcurrentHashMap<String, String> operations, int port) {
		this.members = members;
		this.kvStore = kvStore;
		this.operations = operations;
		this.port = port;
	}



	public void run() {
		System.out.println("thread started");
		while(true){
			StringBuilder sb = new StringBuilder();
			//File file = new File("/tmp/nodes.cfg");
			File file = new File("nodes.txt");
			try {
				sc = new Scanner(file);
				while(sc.hasNextLine()) {
					members.add(sc.nextLine());
					//sb.append(sc.nextLine());
					//sb.append("\n");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//            String memberList = sb.toString();

			System.out.println(members.toString());
			//            members = memberList.split("\n");
			updateConnections();
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void updateConnections() {
		System.out.println("inside updateConnections");
		for(int i = 0; i < members.size()-1; i++) {
			
			try {
				System.out.println(members.get(i));
				String[] info = (members.get(i)).split(":");
				InetAddress ip = InetAddress.getByName(info[0]);
				int port = Integer.valueOf(info[1]);
//				ServerSocket serverSocket = new ServerSocket(port);
				Socket serveSocket = new Socket(ip, port);
				PrintWriter out = new PrintWriter(serveSocket.getOutputStream(), true);
				out.println("hello from leader" + port);
				BufferedReader input = new BufferedReader(new InputStreamReader(serveSocket.getInputStream()));
				String clientInput = input.readLine();
				System.out.println(clientInput);
				String[] message = clientInput.split(" ");
				ThreadHandler thread = new ThreadHandler(serveSocket, message, kvStore, "leaderServer", operations);
				thread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}