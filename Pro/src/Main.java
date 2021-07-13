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
        Pro pro = null;
        System.out.println("Hi " + name + "\nRemember that you are Pro in this game\nTry to kill mafia");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            pro = new Pro(socket , name , in , out);
            String info = pro.getName() + "/" + pro.getType() + "/" + pro.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());
        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        TimeUnit.SECONDS.sleep(2);
        try {
            new DataInputStream(socket.getInputStream()).readUTF();
        } catch (IOException e) {
            System.exit(0);
        }
        pro.chat();
        pro.introduce();
        while (true){
            try {
                if (!pro.getAlive()) {
                    System.out.println("You died!!!");
                    System.exit(1);
                }
                TimeUnit.SECONDS.sleep(2);
                new DataInputStream(socket.getInputStream()).readUTF();
                pro.chat();
                String str = new DataInputStream(socket.getInputStream()).readUTF();
                TimeUnit.SECONDS.sleep(4);
                pro.vote(Integer.parseInt(str));
                new DataInputStream(socket.getInputStream()).readUTF();
                pro.kill();
            }catch (IOException e) {
                System.exit(0);
            }
        }
    }
}
