import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    public Player initial(Socket socket ){
        DataInputStream in;
        try {
            in = new DataInputStream(socket.getInputStream());
            String info = in.readUTF();
            String [] data = info.split("/");
            Player player = new Player(data[0] , data[1] , data[2] , socket);
            return player;
        } catch (IOException e) {
            System.out.println("No socket available");
            System.exit(1);
        }
        return null;
    }
    public void firstNight(HashMap<String , Socket> all , HashMap<String , Socket> mafias , Socket socket){

    }
    public void welcome(){
        System.out.println("Welcome!\nThe game started...\nThe introducing time. introduce yourself");


    }
    public void beReady(Socket socket , Player player){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time is up!");
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error");
                }
            }
        };
        try {
            Timer timer = new Timer();
            timer.schedule(timerTask , 30 * 1000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String str = in.readUTF();
            System.out.println(player.getName() + " is ready");
            timer.cancel();
            timerTask.cancel();
        } catch (IOException e) {
            System.out.println("Error! " + player.getName() + " left the game!");
            return;
        }
    }
}
