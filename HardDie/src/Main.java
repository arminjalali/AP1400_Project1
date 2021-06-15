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
        HardDie hardDie = new HardDie(name);
        System.out.println("Hi " + name + "\nRemember that you are Hard Die in this game\nYou have the opportunity to inquire twice\nAlso your armor is resistant to a single shot fired by the Mafia");
        try {
            socket = new Socket("localhost", 6186);
            hardDie.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = hardDie.getName() + "/" + hardDie.getType() + "/" + hardDie.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());
        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        hardDie.chat(socket);
        while (true){
            if (!hardDie.getAlive()){
                System.out.println("You are died!!!");
                System.exit(1);
            }
            hardDie.chat(socket);
        }
    }
}
