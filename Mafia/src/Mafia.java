import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Mafia {
    private String name;
    private boolean alive;
    private String type;
    private String subType;
    private Socket socket;
    DataOutputStream out;
    DataInputStream input;
    public Mafia(String name){
        this.name = name;
        alive = true;
        type = "Mafia";
        subType = "Simple";
    }

    public void chat(Socket socket) {
        try {
            Scanner get = new Scanner(System.in);
            input = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println(input.readUTF());
            while (true) {
                if (input.readUTF().equals("End")){
                    System.out.println("Time is over");
                    break;
                }
                String str = get.nextLine();
                out.writeUTF(str);
                if (str.equals("0")) {
                    break;
                }
            }
        }
        catch (IOException e){
            System.out.println("Error on connection!");
            System.exit(1);
        }
    }
    public void introduce(){
        try {
            input = new DataInputStream(socket.getInputStream());
            System.out.println(input.readUTF());
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
