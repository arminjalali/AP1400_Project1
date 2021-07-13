import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Game {
    private Player lecterSave;
    private Player psychologistSilence;
    private boolean inquire = false;
    private boolean armor = true;
    private int endOfChat = 0;
    private ArrayList <Player> all;
    private ArrayList <Player> mafia;
    private ArrayList <Player> dead = new ArrayList<>();
    private Player mayor;
    private Player citizen;
    private Player simpleMafia;
    private Player drLecter;
    private Player godFather;
    private Player dieHard;
    private Player psychologist;
    private Player dr;
    private Player detective;
    private Player pro;
    private Player playerReady;
    public Game(ArrayList <Player> all , ArrayList <Player> mafia){
        this.all = all;
        this.mafia = mafia;
    }
    public void setAll(ArrayList<Player> all) {
        this.all = all;
        for (Player player : all){
            if (player.getSubType().equals("Mayor")){
                mayor = player;
            }
            else if (player.getSubType().equals("Dr")){
                dr = player;
            }
            else if (player.getSubType().equals("Mafia")){
                simpleMafia = player;
            }
            else if (player.getSubType().equals("GodFather")){
                godFather = player;
            }
            else if (player.getSubType().equals("DrLecter")){
                drLecter = player;
            }
            else if (player.getSubType().equals("Detective")){
                detective = player;
            }
            else if (player.getSubType().equals("Pro")){
                pro = player;
            }
            else if (player.getSubType().equals("Psychologist")){
                psychologist= player;
            }
            else if (player.getSubType().equals("DieHard")){
                dieHard = player;
            }
        }
    }
    public void info(){
        if (inquire == true){
            inquire = false;
            for (Player player : dead){
                System.out.println("These are dead: \n" + player.getSubType());
            }
        }
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
        System.out.println("Welcome!\nThe game started...\nThe introducing time. introduce yourself in a minute");
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

        public void sendToAll(int size){
            for (Player player : all) {
                try {
                    new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("" + size);
                } catch (IOException e) {
                    System.out.println("Error in connection...");
                }
            }

        }
        public void sendYesOrNoForDefendant(int num , int size){
            for (Player player : all) {
                try {
                    if (num == 0) {
                        new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("0");
                    }
                    else if (num == 1){
                        new DataOutputStream(player.getSocket().getOutputStream()).writeUTF(String.valueOf(size));
                    }
                } catch (IOException e) {
                    System.out.println("Error in connection...");
                }
            }
        }
        public void cancelVoting(ArrayList <Player> defendant) throws IOException {
        for (Player player : defendant){
            new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("");
        }
        }
        public void sendYesToDefendants(ArrayList<Player> defendant){
            for (Player player : all){
                try {
                        if (defendant.contains(player)){
                            new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("1");
                        }
                        else {
                            new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("0");
                        }

                }catch (IOException e){
                    System.out.println("Error!");
                }
            }
        }
        public void specialVote(ArrayList<Player> defendant) throws InterruptedException, IOException {
            ArrayList<Integer> holdVotes = new ArrayList();
            TimeUnit.SECONDS.sleep(2);
            System.out.println("At least " + Math.ceil((double) all.size()/2) + " vote for remove");
            for (int i = 0 ; i < defendant.size() ; i++){
                int vote = 0;
                System.out.println("Vote to " + defendant.get(i).getName());
                for (Player player : all) {
                    if (defendant.contains(player)){
                        continue;
                    }
                    System.out.println(player.getName() + " 15 seconds for vote");
                    String str = null;
                    try {
                        new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("Vote to " + defendant.get(i).getName());
                        str = new DataInputStream(player.getSocket().getInputStream()).readUTF();

                    } catch (IOException e) {
                        System.out.println("Connection lost!");
                    }
                    if (str.equals("1")) {
                        vote += 1;
                    } else {
                        vote += 0;
                    }
                }
                holdVotes.add(vote);
            }
            removeByVote(holdVotes , defendant);
        }
        public void removeByVote(ArrayList<Integer> votes , ArrayList<Player> defendant) throws InterruptedException, IOException {
            int half = (int) Math.ceil((double) all.size()/2);
            int max = 0;
            int draw = 0;
            for (int i = 0 ; i < defendant.size() ; i++){
                if (votes.get(i) > max){
                    max = votes.get(i);
                }
            }
            if (max < half){
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Nobody removed!");
                TimeUnit.SECONDS.sleep(2);
                for(Player player : defendant){
                    new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("No");
                }
                return;
            }
            for (int i = 0 ; i < defendant.size() ; i++){
                if (votes.contains(max)){
                    draw++;
                }
                if (draw>1){
                    System.out.println("Draw!");
                    TimeUnit.SECONDS.sleep(2);
                    for(Player player : defendant){
                        new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("No");
                    }
                    return;
                }
            }
            int num = 0;
            for (int i = 0 ; i < votes.size() ; i++){
                if (votes.get(i).equals(max)){
                    num = i;
                }
                else {
                    new DataOutputStream(defendant.get(i).getSocket().getOutputStream()).writeUTF("No");
                }
            }
            System.out.println("Player " + defendant.get(num).getName() + " you have been removed!\nThe last word");
            TimeUnit.SECONDS.sleep(3);
            new DataOutputStream(defendant.get(num).getSocket().getOutputStream()).writeUTF("Yes");
            ArrayList<Player> removed = new ArrayList<>();
            removed.add(defendant.get(num));
            dead.add(defendant.get(num));
            specialChat(removed);
            TimeUnit.SECONDS.sleep(1);
            all.remove(defendant.get(num));
            mafia.remove(defendant.get(num));
            checkFinish();

        }
        public void globalVote() throws InterruptedException, IOException {
        ArrayList<Integer> holdVotes = new ArrayList();
        System.out.println("Vote time! for every player send 1 to vote");
            TimeUnit.SECONDS.sleep(2);
        for (int i = 0; i < all.size(); i++) {
            String s = "";
            int vote = 0;
            System.out.println("Vote to " + all.get(i).getName());
            for (Player player : all) {
                System.out.println(player.getName() + " 15 seconds for vote");
                String str = null;
                try {
                    new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("Vote to " + all.get(i).getName());
                    str = new DataInputStream(player.getSocket().getInputStream()).readUTF();

                } catch (IOException e) {
                    System.out.println("Connection lost!");
                }
                s += player.getName() + " vote " + str + "\n";
                if (str.equals("1")) {
                    vote += 1;
                } else {
                    vote += 0;
                }
            }
            System.out.println("For " + all.get(i).getName() + " \n" + s);
            holdVotes.add(vote);
        }
            ArrayList<Player> defendants = checkVotes(holdVotes);
            if (defendants.size() == 0){
                TimeUnit.SECONDS.sleep(1);
                System.out.println("No defendant!");
                TimeUnit.SECONDS.sleep(1);
                sendYesOrNoForDefendant(0 , 0);
            }
            else {
                TimeUnit.SECONDS.sleep(1);
                sendYesOrNoForDefendant(1 , defendants.size());
                TimeUnit.SECONDS.sleep(1);
                sendYesToDefendants(defendants);
                TimeUnit.SECONDS.sleep(1);
                specialChat(defendants);
                TimeUnit.SECONDS.sleep(2);
                int MAction = mayorAction();
                if (MAction==100) {
                    weHaveMayorAction(0 , defendants);
                    specialVote(defendants);
                }
                else if (MAction == 200){
                    weHaveMayorAction(1 , defendants);
                    cancelVoting(defendants);
                }
                else {
                    cancelVoting(defendants);
                    weHaveMayorAction(1 , defendants);
                    System.out.println("Player " + all.get(MAction).getName() + " you have been removed!\nThe last word");
                    TimeUnit.SECONDS.sleep(3);
                    new DataOutputStream(all.get(MAction).getSocket().getOutputStream()).writeUTF("Yes");
                    ArrayList<Player> removed = new ArrayList<>();
                    removed.add(all.get(MAction));
                    specialChat(removed);
                    TimeUnit.SECONDS.sleep(1);
                    all.remove(MAction);
                    mafia.remove(removed.get(0));
                    dead.remove(removed.get(0));
                    checkFinish();
                }
                TimeUnit.SECONDS.sleep(2);
            }
    }

    public void globalChat() throws InterruptedException {
        System.out.println("Free chat for all!");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time is over!");
                endOfChat = 1;
            }
        };
        for (int i = 0 ; i < all.size() ; i++){
            new Chat(all.get(i) , psychologistSilence).start();
        }
        timer.schedule(timerTask, 10 * 1000);
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            if (endOfChat == 1) {
                endOfChat = 0;
                timer.cancel();
                timerTask.cancel();
                return;
            }
        }
    }
    public void specialChat(ArrayList <Player> defendant) throws InterruptedException {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time is over!");
                endOfChat = 1;
            }
        };
        for (int i = 0 ; i < defendant.size() ; i++){
            new Chat(defendant.get(i) , psychologistSilence).start();
        }
        timer.schedule(timerTask, 10 * 1000);
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            if (endOfChat == 1) {
                endOfChat = 0;
                timer.cancel();
                timerTask.cancel();
                return;
            }
        }
    }
    public void introduceCitizens(){
        try {
            System.out.println("Mayor wake up");
            new DataOutputStream(mayor.getSocket().getOutputStream()).writeUTF("You are Mayor and " + dr.getName() +" is Dr");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Dr wake up");
            new DataOutputStream(dr.getSocket().getOutputStream()).writeUTF("You are Dr");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Detective wake up");
            new DataOutputStream(detective.getSocket().getOutputStream()).writeUTF("You are Detective");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Pro wake up");
            new DataOutputStream(pro.getSocket().getOutputStream()).writeUTF("You are Pro");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Psychologist wake up");
            new DataOutputStream(psychologist.getSocket().getOutputStream()).writeUTF("You are Psychologist");
            TimeUnit.SECONDS.sleep(5);

            System.out.println("Die-Hard wake up");
            new DataOutputStream(dieHard.getSocket().getOutputStream()).writeUTF("You are Hard-die");
            TimeUnit.SECONDS.sleep(5);

        } catch (IOException e) {
            System.out.println("Error on connection to clients!");
        } catch (InterruptedException e) {
            System.out.println("Error on sleep but go on...");
        }
    }
    public void introduceMafia() throws InterruptedException {

        System.out.println("This is introducing night\nEverybody sleep!");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Mafia members wake up to get to know each other");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Error on sleep! but go on...");
        }
        try {

            String info = simpleMafia.getName() + ": " + simpleMafia.getSubType() +"\n" + godFather.getName() + ": " + godFather.getSubType() + "\n" + drLecter.getName() + ": " + drLecter.getSubType();
            new DataOutputStream(simpleMafia.getSocket().getOutputStream()).writeUTF(info);
            new DataOutputStream(godFather.getSocket().getOutputStream()).writeUTF(info);
            new DataOutputStream(drLecter.getSocket().getOutputStream()).writeUTF(info);
            TimeUnit.SECONDS.sleep(5);
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
    public void mafiaChat() throws InterruptedException, IOException {
        System.out.println("Mafia wake up");
        for (int i = 0 ; i < mafia.size() ; i++){
            new DataOutputStream(mafia.get(i).getSocket().getOutputStream()).writeUTF("1");
            for (Player player : mafia){
                if (!player.equals(mafia.get(i))){
                    new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("2");
                }
            }
            String str = new DataInputStream(mafia.get(i).getSocket().getInputStream()).readUTF();
            for (Player player : mafia){
                if (!player.equals(mafia.get(i))) {
                    new DataOutputStream(player.getSocket().getOutputStream()).writeUTF(mafia.get(i).getName() + " : " + str);
                }
            }
        }
        for (Player player : mafia){
            new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("0");
        }
    }
    public void list(){
        for (int i = 0 ; i < all.size() ; i++){
            System.out.println("[" + (i+1) + "] " + all.get(i).getName());
        }
    }
    public void night() throws IOException, InterruptedException {
        mafiaChat();
        System.out.println("Godfather kill");
        list();
        TimeUnit.SECONDS.sleep(3);
        if (all.contains(godFather)) {
            new DataOutputStream(godFather.getSocket().getOutputStream()).writeUTF("");
            String strGod = new DataInputStream(godFather.getSocket().getInputStream()).readUTF();
            if (strGod != null && Integer.parseInt(strGod) > 0 && Integer.parseInt(strGod) <= all.size() && all.get(Integer.parseInt(strGod) - 1).getType().equals("Citizen")) {
                if (all.get(Integer.parseInt(strGod) - 1).equals(dieHard) && armor) {
                    armor = false;
                } else {
                    all.get(Integer.parseInt(strGod) - 1).kill();
                }
            }
        }
            System.out.println("Dr Lecter save");
            list();
            TimeUnit.SECONDS.sleep(3);
            if (all.contains(drLecter)) {
                new DataOutputStream(drLecter.getSocket().getOutputStream()).writeUTF("");
                String strLec = new DataInputStream(drLecter.getSocket().getInputStream()).readUTF();
                if (strLec != null && Integer.parseInt(strLec) < 11 && Integer.parseInt(strLec) > 0 && all.get(Integer.parseInt(strLec) - 1).getType().equals("Mafia")) {
                    lecterSave = all.get(Integer.parseInt(strLec) - 1);
                }
            }
        System.out.println("Dr wake up and save");
        list();
        TimeUnit.SECONDS.sleep(3);
        if (all.contains(dr)) {
            new DataOutputStream(dr.getSocket().getOutputStream()).writeUTF("");
            String drChoose = new DataInputStream(dr.getSocket().getInputStream()).readUTF();
            int d = Integer.parseInt(drChoose);
            if (drChoose != null && d <= all.size() && d > 0) {
                d = d-1;
                all.get(d).save();
            }
        }
        System.out.println("Detective wake up and find");
        list();
        TimeUnit.SECONDS.sleep(3);
        if (all.contains(detective)) {
            new DataOutputStream(detective.getSocket().getOutputStream()).writeUTF("");
            String choose = new DataInputStream(detective.getSocket().getInputStream()).readUTF();
            int d = Integer.parseInt(choose);
            if (choose != null && d <= all.size() && d > 0) {
                d = d-1;
                if (all.get(d).getType().equals("Citizen")){
                    new DataOutputStream(detective.getSocket().getOutputStream()).writeUTF("No");
                }
                else if (all.get(d).getSubType().equals("GodFather")){
                    new DataOutputStream(detective.getSocket().getOutputStream()).writeUTF("No");
                }
                else if (all.get(d).getType().equals("Mafia")){
                    new DataOutputStream(detective.getSocket().getOutputStream()).writeUTF("Yes");
                }
            }
        }
        System.out.println("Pro wake up and kill");
        list();
        TimeUnit.SECONDS.sleep(3);
        if (all.contains(pro)) {
            new DataOutputStream(pro.getSocket().getOutputStream()).writeUTF("");
            String choose = new DataInputStream(pro.getSocket().getInputStream()).readUTF();
            int d = Integer.parseInt(choose);
            if (choose != null && d <= all.size() && d > 0) {
                d = d-1;
                if (all.get(d).getType().equals("Citizen")){
                    pro.kill();
                }
                else {
                    if (!(all.get(d).equals(lecterSave))){
                        all.get(d).kill();
                    }
                }
            }
        }
        System.out.println("Psychologist wake up and silent");
        list();
        TimeUnit.SECONDS.sleep(3);
        if (all.contains(psychologist)) {
            new DataOutputStream(psychologist.getSocket().getOutputStream()).writeUTF("");
            String choose = new DataInputStream(psychologist.getSocket().getInputStream()).readUTF();
            int d = Integer.parseInt(choose);
            if (choose != null && d <= all.size() && d > 0) {
                d = d-1;
                psychologistSilence = all.get(d);
            }
        }
        System.out.println("DieHard wake up");
        TimeUnit.SECONDS.sleep(3);
        if (all.contains(dieHard)) {
            new DataOutputStream(dieHard.getSocket().getOutputStream()).writeUTF("");
            String str = new DataInputStream(dieHard.getSocket().getInputStream()).readUTF();
            if (str.equals("1")){
                inquire = true;
            }
        }
    }
    public void checkNight(){
        for (int i = 0 ; i < all.size() ; i++){
            Player player = all.get(i);
            if (!player.getAlive()){
                System.out.println(player.getName() + " removed");
                all.remove(player);
                mafia.remove(player);
                dead.add(player);
                checkFinish();
            }
        }
    }
    public ArrayList<Player> checkVotes(ArrayList <Integer> votes){
        ArrayList<Player> defendant = new ArrayList<>();
        int half = (int) Math.ceil((double) all.size()/2);
        for (int i = 0 ; i < votes.size() ; i++){
            if (votes.get(i) >= half){
                defendant.add(all.get(i));
            }
        }
        for (int j = 0 ; j < defendant.size() ; j++){
            System.out.println("Player " + defendant.get(j).getName() + " must defend");
        }
        return defendant;
    }


    public void checkFinish(){
        if (mafia.size() == 0){
            System.out.println("CITIZEN WIN!");
            System.exit(1);
        }
        else if ((int)Math.ceil((double) all.size()/2) == mafia.size()){
            System.out.println("MAFIA WIN");
            System.exit(1);
        }
    }
    public void weHaveMayorAction(int num , ArrayList<Player> def) throws IOException {
        for (Player player : all) {
            if (player.equals(mayor) || def.contains(player)) {
                continue;
            }
            if (num == 1) {
                new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("Mayor");
            }
            else if (num == 0){
                new DataOutputStream(player.getSocket().getOutputStream()).writeUTF("");
            }
        }
    }
    public int mayorAction() throws IOException, InterruptedException {
        DataInputStream in = new DataInputStream(mayor.getSocket().getInputStream());
        DataOutputStream out = new DataOutputStream(mayor.getSocket().getOutputStream());
        System.out.println("Mayor wake up");
        TimeUnit.SECONDS.sleep(2);
        if(!mayor.getAlive()) {
            System.out.println("Mayor sleep");
            TimeUnit.SECONDS.sleep(3);
            return 100;
        }
        out.writeUTF("");
        String doOrNot = in.readUTF();
        if (doOrNot.equals("1")){
            String cancelOrDirect = in.readUTF();
            if (cancelOrDirect.equals("1")){
                System.out.println("The mayor cancelled voting...\nNobody removed");
                TimeUnit.SECONDS.sleep(3);
                return 200;
            }
            else if (cancelOrDirect.equals("2")){
                System.out.println("Direct vote to exit...");
                for (int i = 0 ; i < all.size() ; i++){
                    System.out.println((i + 1)+ ") " + all.get(i).getName() + "\n");
                }
                String player = in.readUTF();
                if (player == null){
                    return 100;
                }
                int choose = Integer.parseInt(player);
                if (choose < 1 || choose > all.size()){
                    return 100;
                }
                else {
                    System.out.println("The mayor decided to remove " + all.get(choose-1).getName());
                    return (choose - 1);
                }
            }
        }
        else if (doOrNot.equals("No more")){
            System.out.println("Mayor already used ability\nMayor sleep");
            TimeUnit.SECONDS.sleep(2);
            return 100;
        }
        System.out.println("Mayor sleep");
        TimeUnit.SECONDS.sleep(3);
        return 100;
    }
}
