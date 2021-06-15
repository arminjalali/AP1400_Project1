import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Game {
    ArrayList <Player> def = null;
    ArrayList <Player> all;
    ArrayList <Player> mafia;
    int counter = 0;
    Player playerReady;
    int flag = 0;
    public Game(ArrayList <Player> all , ArrayList <Player> mafia){
        this.all = all;
        this.mafia = mafia;
    }

    public void setDef(ArrayList<Player> def) {
        this.def = def;
    }

    public Player initial(Socket socket) {
        DataInputStream in;
        try {
            in = new DataInputStream(socket.getInputStream());
            String info = in.readUTF();
            String[] data = info.split("/");
            Player player = new Player(data[0], data[1], data[2], socket);
            return player;
        } catch (IOException e) {
            System.out.println("No socket available");
            System.exit(1);
        }
        return null;
    }

    public void welcome() {
        System.out.println("Welcome!\nThe game started...\nThe introducing time. introduce yourself");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Error on sleep but go on...");
        }
    }

    public void beReady(Socket socket) {
        playerReady = null;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getSocket().equals(socket)) {
                playerReady = all.get(i);
            }
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time is up!");
                try {
                    all.remove(playerReady);
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error");
                }
            }
        };
        try {
            Timer timer = new Timer();
            timer.schedule(timerTask, 30 * 1000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String str = in.readUTF();
            System.out.println(playerReady.getName() + " is ready");
            out.writeUTF("Successful");
            timer.cancel();
            timerTask.cancel();
        } catch (IOException e) {
            System.out.println("Error! " + playerReady.getName() + " left the game!");
            return;
        }
    }

    public void setMafia() {
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getType().equals("Mafia")) {
                mafia.add(all.get(i));
            }
        }
    }

    public void globalVote(ArrayList<Player> arr) {
        ArrayList<Integer> holdVotes = new ArrayList();
        System.out.println("Vote time! for every player send 1 to vote");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Error on sleep but go on...");
        }
        if (arr.size() == 1){
            try {
                Socket s = arr.get(0).getSocket();
                new DataOutputStream(s.getOutputStream()).writeUTF("Finish!");
                new DataInputStream(s.getInputStream()).readUTF();
                new DataOutputStream(s.getOutputStream()).writeUTF("");
            } catch (IOException e) {
                System.out.println("Not found!");
            }
        }
        for (int i = 0; i < arr.size(); i++) {
            int vote = 0;
            System.out.println("Vote to " + arr.get(i).getName());
            try {
                for (int j = 0; j < all.size(); j++) {
                    if (arr.get(i).getName().equals(all.get(j).getName())) {
                        continue;
                    }
                    DataOutputStream out = new DataOutputStream(all.get(j).getSocket().getOutputStream());
                    DataInputStream in = new DataInputStream(all.get(j).getSocket().getInputStream());
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("Time is up!");
                            try {
                                out.writeUTF("Time is over");
                                flag = 1;

                            } catch (IOException e) {
                                System.out.println("Error on write");
                            }
                        }
                    };
                    Timer timer = new Timer();
                    out.writeUTF("Enter 1 to vote");
                    timer.schedule(timerTask, 15 * 1000);
                    String str = in.readUTF();
                    if (str.equals("1")) {
                            System.out.println(all.get(j).getName() + " vote 1");
                            out.writeUTF("Successful");
                            vote++;

                    }
                    else {
                        flag = 0;
                        out.writeUTF("Successful");
                        System.out.println(all.get(j).getName() + " vote 0");
                    }
                    timer.cancel();
                    timerTask.cancel();
                }
            } catch (IOException e) {
                System.out.println("Error on connection!");
            }
            holdVotes.add(vote);
        }
        if (counter == 0) {
            counter = 1;
            ArrayList<Player> defenders = checkVotes(holdVotes);
            setDef(defenders);
            if (defenders != null) {
                try {
                    TimeUnit.SECONDS.sleep(4);
                    globalChat(defenders);
                    TimeUnit.SECONDS.sleep(5);
                    globalVote(defenders);
                    TimeUnit.SECONDS.sleep(5);
                }
                catch (InterruptedException e){
                    System.out.println("Go on...");
                }
            }
        }
        else if (counter == 1){
            counter = 0;
            removeByVote(holdVotes , def);
        }
    }
    public void sendYesToRemove(Player player){
        try {
            new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("Yes");
        } catch (IOException e) {
            System.out.println("Not found!");
        }
    }
    public void removeByVote(ArrayList<Integer> votes, ArrayList<Player> defenders){
        int half = (int) Math.ceil(all.size()/2);
        int counter = 0;
        int counter2 = 0;
        for (int i = 0 ; i < votes.size() ; i++){
            if (votes.get(i) > counter){
                counter = votes.get(i);
            }
        }
        if (counter < half){
            System.out.println("Nobody removed!");
            responseToAllDefenders(defenders);
            return;
        }
        for (int i = 0 ; i < votes.size() ; i++){
            if (votes.get(i).equals(counter)){
                counter2 ++;
            }
        }
        if (counter2 > 1){
            System.out.println("Draw!!!");
            responseToAllDefenders(defenders);
            return;
        }
        for (int i = 0 ; i < votes.size() ; i++){
            if (votes.get(i).equals(counter)){
                System.out.println("Player " + all.get(i).getName() + " you have been removed! your\nThe last word");
                sendYesToRemove(all.get(i));
                defenders.remove(all.get(i));
                responseToAllDefenders(defenders);
                ArrayList <Player> rem = new ArrayList<>();
                rem.add(all.get(i));
                globalChat(rem);
                all.remove(i);
                mafia.remove(rem.get(0));
                checkFinish();
                break;
            }
        }
    }
    public void responseToAllDefenders(ArrayList<Player> defenders){
        for (int i = 0 ; i < defenders.size() ; i++){
            try {
                new DataOutputStream(defenders.get(i).getSocket().getOutputStream()).writeUTF("");
            } catch (IOException e) {
                System.out.println("Error!");
            }
        }
    }
    public void globalChat(ArrayList<Player> all) {
        flag = 0;

            try {
                for (int i = 0; i < all.size(); i++) {
                    DataOutputStream out;
                    DataInputStream in;
                    DataOutputStream finalOut = new DataOutputStream(all.get(i).getSocket().getOutputStream());
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("Time is up!");
                            try {
                                finalOut.writeUTF("End");
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
                    timer.schedule(timerTask, 30 * 1000);
                    while (true) {
                        if (flag == 1) {
                            System.out.println("flag?");
                            flag = 0;
                            break;
                        }
                        out.writeUTF("");
                        String str = in.readUTF();
                        if (flag == 1) {
                            flag = 0;
                            break;
                        }
                        if (str.equals("0")) {
                            System.out.println("End of " + all.get(i).getName() + "'s speech");
                            break;
                        }
                        System.out.println(all.get(i).getName() + ": " + str);
                    }
                    timer.cancel();
                    timerTask.cancel();
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (IOException e) {
                System.out.println("Error on connection!");
                System.exit(1);
            } catch (InterruptedException e) {
                System.out.println("Error on sleep! but go on...");
            }
    }
    public void introduceCitizens(){

        Player M = null;
        Player Dr = null;
        Player Det = null;
        Player Pro = null;
        Player Psy = null;
        Player H = null;
        for (int i = 0 ; i < all.size() ; i++){
            if (all.get(i).getSubType().equals("Mayor")){
                M = all.get(i);
            }
            if (all.get(i).getSubType().equals("Dr")){
                Dr = all.get(i);
            }
            if (all.get(i).getSubType().equals("Detective")){
                Det = all.get(i);
            }
            if (all.get(i).getSubType().equals("Pro")){
                Pro = all.get(i);
            }
            if (all.get(i).getSubType().equals("Psychologist")){
                Psy = all.get(i);
            }
            if (all.get(i).getSubType().equals("HardDie")){
                H = all.get(i);
            }
        }
        try {
            System.out.println("Mayor wake up");
            new DataOutputStream(M.getSocket().getOutputStream()).writeUTF("You are Mayor and " + Dr.getName() + " is Dr");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Dr wake up");
            new DataOutputStream(Dr.getSocket().getOutputStream()).writeUTF("You are Dr");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Detective wake up");
            new DataOutputStream(Det.getSocket().getOutputStream()).writeUTF("You are Detective");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Pro wake up");
            new DataOutputStream(Pro.getSocket().getOutputStream()).writeUTF("You are Pro");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Psychologist wake up");
            new DataOutputStream(Psy.getSocket().getOutputStream()).writeUTF("You are Psychologist");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Hard-die wake up");
            new DataOutputStream(H.getSocket().getOutputStream()).writeUTF("You are Hard-die");
            TimeUnit.SECONDS.sleep(5);

        } catch (IOException e) {
            System.out.println("Error on connection to clients!");
        } catch (InterruptedException e) {
            System.out.println("Error on sleep but go on...");
        }
    }
    public void introduceMafia(){

        System.out.println("This is introducing night\nEverybody sleep!");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Mafia members wake up to get to know each other");
            TimeUnit.SECONDS.sleep(5);
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
            String info = S.getName() + ": " + S.getSubType() +"\n" + G.getName() + ": " + G.getSubType() + "\n" + D.getName() + ": " + D.getSubType();
            new DataOutputStream(S.getSocket().getOutputStream()).writeUTF(info);
            new DataOutputStream(G.getSocket().getOutputStream()).writeUTF(info);
            new DataOutputStream(D.getSocket().getOutputStream()).writeUTF(info);
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
    public void night(){

    }
    public ArrayList<Player> checkVotes(ArrayList <Integer> votes){
        ArrayList<Player> defender = new ArrayList<>();
        int half = (int) Math.ceil(all.size()/2);
        for (int i = 0 ; i < votes.size() ; i++){
            if (votes.get(i) >= half){
                defender.add(all.get(i));
            }
        }
        for (int j = 0 ; j < defender.size() ; j++){
            System.out.println("Player " + defender.get(j).getName() + " must defend");
        }
        sendYes(defender);
        return defender;
    }
    public void sendYes(ArrayList<Player> defender) {
        for (int j = 0; j < all.size(); j++) {
            int counter = 0;
            for (int i = 0 ; i < defender.size() ; i++){
                if (all.get(j).getName().equals(defender.get(i).getName())){
                    counter++;
                    break;
                }
            }
                try {
                    if (counter != 0) {
                        new DataOutputStream(all.get(j).getSocket().getOutputStream()).writeUTF("Yes");
                    }
                    else {
                        new DataOutputStream(all.get(j).getSocket().getOutputStream()).writeUTF("");
                    }
                } catch (IOException e) {
                    System.out.println("Error!");
                }
        }
    }
    public void checkFinish(){
        if (mafia.size() == 0){
            System.out.println("CITIZEN WIN!");
            System.exit(1);
        }
        else if ((int)Math.ceil(all.size()/2) == mafia.size()){
            System.out.println("MAFIA WIN");
            System.exit(1);
        }
    }
}
