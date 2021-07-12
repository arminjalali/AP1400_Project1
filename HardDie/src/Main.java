import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        DataOutputStream out;
        DataInputStream in;
        Scanner get = new Scanner(System.in);
        Socket socket = null;
        System.out.println("Enter your name");
        String name = get.nextLine();
        HardDie hardDie = null;
        System.out.println("Hi " + name + "\nRemember that you are Hard Die in this game\nYou have the opportunity to inquire twice\nAlso your armor is resistant to a single shot fired by the Mafia");
        try {
            socket = new Socket("localhost", 6186);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            hardDie = new HardDie(socket , name , in , out);
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
        TimeUnit.SECONDS.sleep(2);
        new DataInputStream(socket.getInputStream()).readUTF();
        hardDie.chat();
        hardDie.introduce();
        while (true){
            if (!hardDie.getAlive()){
                System.out.println("You died!!!");
                System.exit(1);
            }
            TimeUnit.SECONDS.sleep(2);
            new DataInputStream(socket.getInputStream()).readUTF();
            hardDie.chat();
            String str = null;
            try {
                str = new DataInputStream(socket.getInputStream()).readUTF();
            } catch (IOException e) {
                System.out.println("Error in connection!");
                System.exit(0);
            }
            TimeUnit.SECONDS.sleep(4);
            hardDie.vote(Integer.parseInt(str));
            try {
                new DataInputStream(socket.getInputStream()).readUTF();
            } catch (IOException e) {
                System.out.println("Error in connection!");
                System.exit(0);
            }
        }
    }
}
