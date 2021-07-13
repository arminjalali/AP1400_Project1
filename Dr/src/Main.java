import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        Dr dr = null;
        System.out.println("Hi " + name + "\nRemember that you are Dr in this game\nTry to save citizens");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            dr = new Dr(socket, name, in, out);
            String info = dr.getName() + "/" + dr.getType() + "/" + dr.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());
        } catch (IOException e) {
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        try {
            TimeUnit.SECONDS.sleep(2);
            new DataInputStream(socket.getInputStream()).readUTF();
            dr.chat();
            dr.introduce();
            while (true) {
                if (!dr.getAlive()) {
                    System.out.println("You died!!!");
                    System.exit(1);
                }
                TimeUnit.SECONDS.sleep(2);
                new DataInputStream(socket.getInputStream()).readUTF();
                dr.chat();
                String str = new DataInputStream(socket.getInputStream()).readUTF();
                TimeUnit.SECONDS.sleep(4);
                dr.vote(Integer.parseInt(str));
                new DataInputStream(socket.getInputStream()).readUTF();
                dr.save();

            }
        }catch (IOException ex){
            System.out.println("Error!");
            System.exit(0);
        }
    }
}
