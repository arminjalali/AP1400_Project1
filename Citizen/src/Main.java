import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        DataOutputStream out;
        DataInputStream in;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        Citizen citizen = null;
        System.out.println("Hi " + name + "\nRemember that you are simple Citizen in this game\nJust vote to kill Mafia");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            citizen = new Citizen(socket, name, in, out);
            String info = citizen.getName() + "/" + citizen.getType() + "/" + citizen.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());
        } catch (IOException e) {
            System.out.println("Error in connection to the server!");
            System.exit(1);
        }
        TimeUnit.SECONDS.sleep(2);
        new DataInputStream(socket.getInputStream()).readUTF();
        citizen.chat();
        while (true) {
            try {
                if (!citizen.getAlive()) {
                    System.out.println("You died!!!");
                    System.exit(1);
                }
                TimeUnit.SECONDS.sleep(2);
                new DataInputStream(socket.getInputStream()).readUTF();
                citizen.chat();
                String str = new DataInputStream(socket.getInputStream()).readUTF();
                TimeUnit.SECONDS.sleep(4);
                citizen.vote(Integer.parseInt(str));
                new DataInputStream(socket.getInputStream()).readUTF();
            }catch (IOException e) {
                System.exit(0);
            }
        }
    }
}
