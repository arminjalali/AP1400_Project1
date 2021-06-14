import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalChat {
    private DataOutputStream out;
    private DataInputStream in;
    private ArrayList <Player> all;
    int flag = 0;
    public GlobalChat(ArrayList<Player> all){
        this.all = all;
    }

    public void run() {
        try {
            for(int i = 0 ; i < all.size() ; i++){
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Time is up!");
                        try {
                            out.writeUTF("End");
                        } catch (IOException e) {
                            System.out.println("Error on write");
                        }
                        flag = 1;

                    }
                };
                out = new DataOutputStream(all.get(i).getSocket().getOutputStream());
                out.writeUTF("30 second for talk and enter '0' to end");
                System.out.println(all.get(i).getName() + " it's your turn to talk");
                Timer timer = new Timer();
                in = new DataInputStream(all.get(i).getSocket().getInputStream());
                timer.schedule(timerTask , 30*1000);
                while (true){
                    if (flag == 1 ){
                        flag = 0;
                        break;
                    }
                    out.writeUTF("");
                    String str = in.readUTF();
                    if (flag == 1 ){
                        flag = 0;
                        break;
                    }
                    if (str.equals("0")){
                        System.out.println("End of " + all.get(i).getName() + "'s speech");
                        break;
                    }
                    System.out.println(all.get(i).getName() + ": " + str);
                }
                timer.cancel();
                timerTask.cancel();
            }
        }
        catch (IOException e){
            System.out.println("Error on connection!");
            System.exit(1);
        }
    }
}
