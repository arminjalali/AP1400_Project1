import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        DataOutputStream out;
        DataInputStream in;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        DrLecter drLecter = null;
        System.out.println("Hi " + name + "\nRemember that you are dr lecter in this game\ntry to save mafia");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            drLecter = new DrLecter(socket, name, in, out);
            String info = drLecter.getName() + "/" + drLecter.getType() + "/" + drLecter.getSubType();
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
        drLecter.chat();
        drLecter.introduce();
        while (true) {
            if (!drLecter.getAlive()) {
                System.out.println("You died!!!");
                System.exit(1);
            }
            TimeUnit.SECONDS.sleep(2);
            new DataInputStream(socket.getInputStream()).readUTF();
            drLecter.chat();
            String str = new DataInputStream(socket.getInputStream()).readUTF();
            TimeUnit.SECONDS.sleep(4);
            drLecter.vote(Integer.parseInt(str));
            new DataInputStream(socket.getInputStream()).readUTF();
            drLecter.nightChat();
        }
    }
}
