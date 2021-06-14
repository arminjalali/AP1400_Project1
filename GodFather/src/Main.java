import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            socket = new Socket("localhost", 6185);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Scanner get = new Scanner(System.in);
            String text;
            while (true) {
                text = get.nextLine();
                out.writeUTF(text);
                if (text.equals("over")){
                    break;
                }
            }
            String text1;
            while (!(text1 = get.nextLine()).equals("O")) {
                System.out.println("See it?");
                out.writeUTF(text1);
                System.out.println(in.readUTF());
            }
            socket.close();
            in.close();
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
