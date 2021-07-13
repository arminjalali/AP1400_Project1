import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ServerSocket server = null;
        ArrayList <Player> all = new ArrayList<>();
        ArrayList <Player> mafia = new ArrayList<>();
        Game game = new Game(all , mafia);
        try {
            server = new ServerSocket(6186);
            System.out.println("Server started...");
        }
        catch (IOException e){
            System.out.println("Error on start the server!");
            System.exit(1);
        }
        int counter = 0;
            try {
                while (counter != 10) {
                    Socket socket = server.accept();
                    all.add(game.initial(socket));
                    game.beReady(socket);
                    counter ++;
                }
                int day = 0;
                game.setAll(all);
                game.setMafia();
                game.sendToAll(all.size());
                game.welcome();
                game.globalChat();
                game.introduceMafia();
                game.introduceCitizens();
                while (true){
                    day++;
                    System.out.println("Day number: " + day);
                    TimeUnit.SECONDS.sleep(4);
                    game.info();
                    TimeUnit.SECONDS.sleep(5);
                    game.sendToAll(all.size());
                    game.globalChat();
                    TimeUnit.SECONDS.sleep(4);
                    game.sendToAll(all.size());
                    game.globalVote();
                    game.sendToAll(all.size());
                    game.night();
                    game.checkNight();



                }

            } catch (IOException e) {
                System.out.println("Error on connecting to clients!");
                System.exit(1);
            } catch (InterruptedException e) {
                System.out.println("Error on sleep but go on...");
            }

    }
}
