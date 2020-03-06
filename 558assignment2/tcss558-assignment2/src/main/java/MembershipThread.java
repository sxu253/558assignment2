import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MembershipThread extends Thread {
    String[] members;
    public MembershipThread(String[] members) {
        this.members = members;
    }
    public void run() {
        while(true){
            StringBuilder sb = new StringBuilder();
            File file = new File("/tmp/nodes.cfg");
            try {
                Scanner sc = new Scanner(file);
                while(sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String memberList = sb.toString();
            members = memberList.split("\n");
            try {
                sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
