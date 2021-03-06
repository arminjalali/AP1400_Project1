import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        DrLecter drLecter = new DrLecter(name);
        System.out.println("Hi " + name + "\nRemember that you are Dr Lecter in this game\nTry to save mafias!");
        try {
            socket = new Socket("localhost", 6186);
            drLecter.setSocket(socket);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = drLecter.getName() + "/" + drLecter.getType() + "/" + drLecter.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());

        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        drLecter.chat(socket);
        drLecter.introduce();
        while (true){
            if (!drLecter.getAlive()){
                System.out.println("You died!!!");
                System.exit(1);
            }
            drLecter.chat(socket);
            drLecter.vote();

        }
    }
}
