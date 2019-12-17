package view;

public class ViewGameInfo {
    private String masterIp;
    private int playersAmount;
    private int fieldWidth, fieldHeigth;

    public ViewGameInfo(String masterIp, int playersAmount, int fieldWidth, int fieldHeigth){
        this.masterIp = masterIp;
        this.playersAmount = playersAmount;
        this.fieldWidth = fieldWidth;
        this.fieldHeigth = fieldHeigth;
    }

    int getFieldHeigth() {
        return fieldHeigth;
    }

    int getFieldWidth() {
        return fieldWidth;
    }

    int getPlayersAmount() {
        return playersAmount;
    }

    String getMasterIp() {
        return masterIp;
    }

    @Override
    public String toString() {
        return masterIp + " " +
                playersAmount + " " + fieldWidth + " X " + fieldHeigth;
    }
}
