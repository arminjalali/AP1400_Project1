import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Chat extends Thread{

    Player player;
    Player silent;
    public Chat(Player player , Player silent){
        this.player = player;
        this.silent = silent;
    }
    @Override
    public void run() {
        while (true) {
            try {
                String str = new DataInputStream(player.getSocket().getInputStream()).readUTF();
                if (str.equals("EndOfChat")) {
                    return;
                }
                if (silent == null){
                    System.out.println(player.getName() + ": " + str);
                }
                else if (!player.equals(silent)) {
                    System.out.println(player.getName() + ": " + str);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
