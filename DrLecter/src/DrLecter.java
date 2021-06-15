import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class DrLecter{
    int flag = 0;
    private String name;
    private boolean alive;
    private String type;
    private String subType;
    private Socket socket;

    public DrLecter(String name) {
        this.name = name;
        alive = true;
        type = "Mafia";
        subType = "DrLecter";
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


    public void chat(Socket socket) {
        try {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("");
                        flag = 1;
                    } catch (IOException e) {
                        System.out.println("Error on socket...");
                    }
                }
            };
            Timer timer = new Timer();
            Scanner get = new Scanner(System.in);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println(input.readUTF());
            timer.schedule(timerTask , 35 * 1000);
            while (true) {
                if (input.readUTF().equals("End")) {
                    System.out.println("Time is over");
                    timer.cancel();
                    timerTask.cancel();
                    flag = 0;
                    break;
                }
                String str = get.nextLine();
                if (flag !=0){
                    continue;
                }
                out.writeUTF(str);
                if (str.equals("0")) {
                    timer.cancel();
                    timerTask.cancel();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error on connection!");
            System.exit(1);
        }
    }
    public void vote(){
        try {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("");
                        flag = 1;
                    } catch (IOException e) {
                        System.out.println("Error on socket...");
                    }
                }
            };
            Timer timer = new Timer();
            Scanner get = new Scanner(System.in);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            System.out.println(in.readUTF());
            timer.schedule(timerTask , 16 * 1000);
            String str = get.nextLine();
            if (flag != 0){
                flag = 0;
            }
            else {
                out.writeUTF(str);
                System.out.println(in.readUTF());
            }
            timer.cancel();
            timerTask.cancel();
        } catch (IOException e) {
            System.out.println("Error on vote");
        }
        doIDefend();
    }
    public void doIDefend(){
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String str = in.readUTF();
            if (str.equals("Yes")){
                System.out.println("You must defend yourself!!!");
                chat(socket);
                vote();
                String str2 = in.readUTF();
                if (str2.equals("Yes")){
                    chat(socket);
                    alive = false;
                }
                else {
                    System.out.println("You survived!");
                }
            }
        } catch (IOException e) {
            System.out.println("Error on connection!");
        }
    }
}

