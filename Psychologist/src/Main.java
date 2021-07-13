import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException{
        DataOutputStream out;
        DataInputStream in;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        Psychologist psychologist = null;
        System.out.println("Hi " + name + "\nRemember that you are Psychologist in this game\nTry to help citizens");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            psychologist = new Psychologist(socket , name , in , out);
            String info = psychologist.getName() + "/" + psychologist.getType() + "/" + psychologist.getSubType();
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
        psychologist.chat();
        psychologist.introduce();
        while (true) {
            try {
                if (!psychologist.getAlive()) {
                    System.out.println("You died!!!");
                    System.exit(1);
                }
                TimeUnit.SECONDS.sleep(2);
                new DataInputStream(socket.getInputStream()).readUTF();
                psychologist.chat();
                String str = new DataInputStream(socket.getInputStream()).readUTF();
                TimeUnit.SECONDS.sleep(4);
                psychologist.vote(Integer.parseInt(str));
                new DataInputStream(socket.getInputStream()).readUTF();
                psychologist.silence();
            }catch (IOException e){
                System.exit(0);
            }
        }
    }
}
