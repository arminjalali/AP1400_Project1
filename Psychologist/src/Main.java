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
        Psychologist psychologist = new Psychologist(name);
        System.out.println("Hi " + name + "\nRemember that you are Psychologist in this game\nSilent anyone you want twice");
        try {
            socket = new Socket("localhost", 6186);
            psychologist.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
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
        psychologist.chat(socket);
        psychologist.introduce();
        while (true){
            if (!psychologist.getAlive()){
                System.out.println("You died!!!");
                System.exit(1);
            }
            psychologist.chat(socket);
            psychologist.vote();

        }
        }
    }
}
