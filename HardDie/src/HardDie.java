import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class HardDie {
    private String name;
    private boolean alive;
    private boolean armor;
    private String type;
    private String subType;
    private Socket socket;
    private int inquire;

    public HardDie(String name) {
        this.name = name;
        alive = true;
        armor = true;
        inquire = 2;
        type = "Citizen";
        subType = "HardDie";
    }

    public int getInquire() {
        return inquire;
    }

    public boolean getArmor() {
        return armor;
    }

    public boolean getAlive(){
        return alive;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
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


    public void chat(Socket socket) {
        try {
            Scanner get = new Scanner(System.in);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
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

}