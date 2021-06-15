import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DataOutputStream out = null;
        DataInputStream in = null;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        Pro pro = new Pro(name);
        System.out.println("Hi " + name + "\nRemember that you are Pro or Sniper in this game\nTry to shot Mafia with your 2 bullets");
        try {
            socket = new Socket("localhost", 6186);
            pro.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
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
        pro.chat(socket);
        pro.introduce();
        while (true){
            if (!pro.getAlive()){
                System.out.println("You died!!!");
                System.exit(1);
            }
            pro.chat(socket);
            pro.vote();

        }
    }
}
