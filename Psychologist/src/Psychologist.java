import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Psychologist {
    private  int endOfChat = 0;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private boolean alive;
    private String type;
    private String subType;
    private Socket socket;
    private int bullet;

    public Psychologist(Socket socket , String name , DataInputStream in , DataOutputStream out) {
        this.socket = socket;
        this.name = name;
        alive = true;
        type = "Citizen";
        subType = "Psychologist";
        this.out = out;
        this.in = in;
        bullet = 2;
    }

    public void introduce() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            System.out.println(input.readUTF());
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    public boolean getAlive() {
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

    public void silence() throws InterruptedException, IOException {
        new DataInputStream(socket.getInputStream()).readUTF();
        if (bullet < 1) {
            new DataOutputStream(socket.getOutputStream()).writeUTF("100");
            return;
        }
        Execute ex = new Execute(1);
        System.out.println("Choose");
        String str = ex.readLine();
        if (str != null) {
            bullet--;
            new DataOutputStream(socket.getOutputStream()).writeUTF(str);
        }
        else{
            new DataOutputStream(socket.getOutputStream()).writeUTF("100");
        }
    }
    public void chat() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                endOfChat = 1;
            }
        };
        try {
            System.out.println("You can chat");
            timer.schedule(timerTask ,10 * 1000);
            Execute ex = new Execute(1);
            while (true) {
                String str = ex.readLine();
                if (endOfChat == 1){
                    endOfChat = 0;
                    timer.cancel();
                    out.writeUTF("EndOfChat");
                    return;
                }
                out.writeUTF(str);
            }
        }
        catch(IOException | InterruptedException e){
            System.out.println("Error on connection!");
        }
    }
    public void vote(int size) throws InterruptedException, IOException {
        for (int i = 0; i < size; i++) {
            try {
                System.out.println(in.readUTF());
            } catch (IOException e) {
                System.out.println("Error in connecting to the server!");
            }
            Execute ex = new Execute(0);
            String str = ex.readLine();
            if (str == null) {
                out.writeUTF("0");
                continue;
            }
            try {
                out.writeUTF(str);
            } catch (IOException e) {
                System.out.println("Closed!");
            }
        }
        int sizeOfDefender;
        if ((sizeOfDefender = haveDefender()) != 0){
            System.out.println("We have defendant!");
            if (doIDefend()){
                System.out.println("You must defend yourself!");
                chat();
                if (checkIRemoved()){
                    System.out.println("Last word...");
                    chat();
                    alive = false;
                }
            }
            else {
                if (!(in.readUTF().equals("Mayor"))) {
                    specialVote(sizeOfDefender);
                }
            }
        }
    }
    public void specialVote(int size) throws InterruptedException, IOException {
        for (int i = 0; i < size; i++) {
            try {
                System.out.println(in.readUTF());
            } catch (IOException e) {
                System.out.println("Error in connecting to the server!");
            }
            Execute ex = new Execute(0);
            String str = ex.readLine();
            if (str == null) {
                out.writeUTF("0");
                continue;
            }
            try {
                out.writeUTF(str);
            } catch (IOException e) {
                System.out.println("Closed!");
            }
        }
    }
    public boolean checkIRemoved() throws IOException {
        return in.readUTF().equals("Yes");
    }
    public int haveDefender() throws IOException {
        return Integer.parseInt(in.readUTF());
    }
    public boolean doIDefend() throws IOException {
        return in.readUTF().equals("1");
    }
}

