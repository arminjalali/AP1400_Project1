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
        Citizen citizen = new Citizen(name);
        System.out.println("Hi " + name + "\nRemember that you are simple Citizen in this game\nJust vote to kill Mafia");
        try {
            socket = new Socket("localhost", 6186);
            citizen.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = citizen.getName() + "/" + citizen.getType() + "/" + citizen.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());
        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        citizen.chat(socket);
        while (true){
            if (!citizen.getAlive()){
                System.out.println("You are died!!!");
                System.exit(1);
            }
            citizen.chat(socket);
        }
    }
}
