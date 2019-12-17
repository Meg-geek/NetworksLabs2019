package view;

public class ViewPlayerInfo {
    private String name;
    private int score;
    private boolean isMaster;

    public ViewPlayerInfo(String name, int score, boolean isMaster){
        this.name = name;
        this.score = score;
        this.isMaster = isMaster;
    }

    String getName(){
        return name;
    }

    int getScore(){
        return score;
    }

    boolean isMaster(){
        return isMaster;
    }
}
