import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
        GodFather godFather = new GodFather(name);
        System.out.println("Hi " + name + "\nRemember that you are God Father in this game\nKill citizens");
        try {
            socket = new Socket("localhost", 6186);
            godFather.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = godFather.getName() + "/" + godFather.getType() + "/" + godFather.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());

        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        godFather.chat(socket);
        godFather.introduce();
        while (true){
            if (!godFather.getAlive()){
                System.out.println("You died!!!");
                System.exit(1);
            }
            godFather.chat(socket);
            godFather.vote();

        }
    }
}
