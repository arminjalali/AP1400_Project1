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
        Detective detective = new Detective(name);
        System.out.println("Hi " + name + "\nRemember that you are Dr Lecter in this game\nTry to save mafias!");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String info = detective.getName() + "/" + detective.getType() + "/" + detective.getSubType();
            out.writeUTF(info);
            System.out.println("Enter a key to start");
            out.writeUTF(get.nextLine());
            System.out.println(in.readUTF());

        }
        catch (IOException e){
            System.out.println("Error on connection to the server!");
            System.exit(1);
        }
        detective.chat(socket);
        while (true){}
    }
}
