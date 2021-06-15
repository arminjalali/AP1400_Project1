import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        DataOutputStream out = null;
        DataInputStream in = null;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        Mayor mayor = new Mayor(name);
        System.out.println("Hi " + name + "\nRemember that you are Mayor in this game\nTry to handle votes and save Dr");
        try {
            socket = new Socket("localhost", 6186);
            mayor.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = mayor.getName() + "/" + mayor.getType() + "/" + mayor.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());
        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        mayor.chat(socket);
        mayor.introduce();
        while (true){
            if (!mayor.getAlive()){
                System.out.println("You died!!!");
                System.exit(1);
            }
            mayor.chat(socket);
            mayor.vote();

        }
    }
}
