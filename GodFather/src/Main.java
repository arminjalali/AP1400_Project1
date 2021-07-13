import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        DataOutputStream out;
        DataInputStream in;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        GodFather godFather = null;
        System.out.println("Hi " + name + "\nRemember that you are god father in this game\ntry to kill citizens");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            godFather = new GodFather(socket, name, in, out);
            String info = godFather.getName() + "/" + godFather.getType() + "/" + godFather.getSubType();
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
        godFather.chat();
        godFather.introduce();
        while (true) {
            try {
                if (!godFather.getAlive()) {
                    System.out.println("You died!!!");
                    System.exit(1);
                }
                TimeUnit.SECONDS.sleep(2);
                new DataInputStream(socket.getInputStream()).readUTF();
                godFather.chat();
                String str = new DataInputStream(socket.getInputStream()).readUTF();
                TimeUnit.SECONDS.sleep(4);
                godFather.vote(Integer.parseInt(str));
                new DataInputStream(socket.getInputStream()).readUTF();
                godFather.nightChat();
                new DataInputStream(socket.getInputStream()).readUTF();
                godFather.kill();
            }catch (IOException e) {
                System.exit(0);
            }
        }
    }
}
