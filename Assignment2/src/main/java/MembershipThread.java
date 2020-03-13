import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads the membership text file every 15 seconds and updates the node's membership list
 */
public class MembershipThread extends Thread {
    private ConcurrentHashMap<Integer, InetAddress> members;
    private Scanner sc;
    private int myPort;

    /**
     * Constructor for the membership thread
     * @param members the HashMap storing the other members in the network
     */
    public MembershipThread(ConcurrentHashMap<Integer, InetAddress> members, int myPort) {
        this.members = members;
        this.myPort = myPort;
    }

    public void run() {
        ConcurrentHashMap<Integer, InetAddress> newMembers = new ConcurrentHashMap<>();
        while (true) {
            File file = new File("nodes.cfg");
            try {
                sc = new Scanner(file);
                if(!newMembers.isEmpty())
                    newMembers.clear();
                while (sc.hasNextLine()) {
                    String[] info = (sc.nextLine().split(":"));
                    InetAddress ip = InetAddress.getByName(info[0]);
                    int port = Integer.valueOf(info[1]);
                    if(port != myPort)
                        newMembers.put(port, ip);
                }
                members = newMembers;
            } catch (FileNotFoundException e) {
                System.out.println("file was not found");
            }
            catch (UnknownHostException e) {
                System.out.println("host not found exception");
            }

            try {
                sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("couldn't sleep :(");
            }
        }
    }
}