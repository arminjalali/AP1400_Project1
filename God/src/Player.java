import java.net.Socket;

public class Player {
    private String name;
    private boolean alive;
    private String type;
    private String subType;
    private Socket socket;
    public Player(String name , String type , String subType , Socket socket){
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.socket = socket;
        alive = true;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", alive=" + alive +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", socket=" + socket +
                '}';
    }
}
