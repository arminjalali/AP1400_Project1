import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DataInputStream in;
        DataOutputStream out;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        Mafia mafia = new Mafia(name);
        System.out.println("Hi " + name + "\nRemember that you are simple Mafia in this game\nTry to kill citizens");
        try {
            socket = new Socket("localhost", 6186);
            mafia.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = mafia.getName() + "/" + mafia.getType() + "/" + mafia.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());

        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        mafia.chat(socket);
        mafia.introduce();
        while (true){}
    }
}
