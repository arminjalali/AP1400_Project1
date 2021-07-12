import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Chat extends Thread{

    Player player;
    public Chat(Player player){
        this.player = player;
    }
    @Override
    public void run() {
        while (true) {
            try {
                String str = new DataInputStream(player.getSocket().getInputStream()).readUTF();
                if (str.equals("EndOfChat")) {
                    return;
                }
                System.out.println(player.getName() + ": " + str);
            } catch (IOException ignored) {
            }
        }
    }
}
