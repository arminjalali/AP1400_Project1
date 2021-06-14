import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class BeReady extends Thread{
    Socket socket;
    Player player;
    public BeReady(Socket socket , Player player){
        this.socket = socket;
        this.player = player;
    }
    @Override
    public void run(){
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
