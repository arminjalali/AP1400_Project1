import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();
        ServerSocket server = null;
        ArrayList <Player> all = new ArrayList<>();
        ArrayList <Player> mafias = new ArrayList<>();
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
                while (counter != 1) {
                    Socket socket = server.accept();
                    all.add(game.initial(socket));
                    game.beReady(socket,all.get(counter));
                    counter++;
                }
                    new GlobalChat(all).run();
            } catch (IOException e) {
                System.out.println("Error on connecting to clients!");
                System.exit(1);
            }

    }
}
