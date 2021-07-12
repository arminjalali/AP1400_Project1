import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        Mafia mafia = null;
        System.out.println("Hi " + name + "\nRemember that you are simple mafia in this game\ntry to kill citizens");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            mafia = new Mafia(socket, name, in, out);
            String info = mafia.getName() + "/" + mafia.getType() + "/" + mafia.getSubType();
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
        mafia.chat();
        mafia.introduce();
        while (true) {
            if (!mafia.getAlive()) {
                System.out.println("You died!!!");
                System.exit(1);
            }
            TimeUnit.SECONDS.sleep(2);
            new DataInputStream(socket.getInputStream()).readUTF();
            mafia.chat();
            String str = new DataInputStream(socket.getInputStream()).readUTF();
            TimeUnit.SECONDS.sleep(4);
            mafia.vote(Integer.parseInt(str));
            new DataInputStream(socket.getInputStream()).readUTF();
            mafia.nightChat();

        }
    }
}
