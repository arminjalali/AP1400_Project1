import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MafiaChat{
    DataInputStream in;
    DataOutputStream out;
    ArrayList <Player> mafia;
    public MafiaChat(ArrayList<Player> mafia){
        this.mafia = mafia;
    }
    public void introduceMafia(){
        System.out.println("This is introducing night\nEverybody sleep!");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Mafia members wake up to get to know each other");
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            System.out.println("Error on sleep! but go on...");
        }
            try {
                Player S = null;
                Player G = null;
                Player D = null;
                for (int i = 0 ; i < mafia.size() ; i++) {
                    if (mafia.get(i).getSubType().equals("GodFather")){
                        G = mafia.get(i);
                    }
                    if (mafia.get(i).getSubType().equals("DrLecter")){
                        D = mafia.get(i);
                    }
                    if (mafia.get(i).getSubType().equals("Simple")){
                        S = mafia.get(i);
                    }
                }
                String info = S.getName() + ": " + S.getSubType() +"\n" + /*G.getName() + ": " + G.getSubType() + "\n" +*/ D.getName() + ": " + D.getSubType();
                new DataOutputStream(S.getSocket().getOutputStream()).writeUTF(info);
              //  new DataOutputStream(G.getSocket().getOutputStream()).writeUTF(info);
                new DataOutputStream(D.getSocket().getOutputStream()).writeUTF(info);
            } catch (IOException e) {
                System.out.println("Error!");
            }
    }
}
