import java.io.Serializable;

public class DrLecter implements Serializable {
    private String name;
    private boolean alive;
    private String type;
    private String subType;
    public DrLecter(String name){
        this.name = name;
        alive = true;
        type = "Mafia";
        subType = "DrLecter";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

}
