package model.networkUtils;

import model.game.Direction;
import model.game.GameSettings;

import java.util.List;

public interface NetworkGame {
    void handleMessage(Message message);

    //long getAndIncrementMsgSeq();
    //int getMyID();
   // List<NetworkUser> getUsersList();
    GameNetworkSettings getNetworkSettings();
    GameSettings getGameSettings();
    boolean equals(NetworkGame game);
    //String getMasterIP();
    MasterNode getMaster();
    void sendMessage(Message message, List<NetworkUser> usersList);
    void setSnakeDirection(Direction direction);
    void quitGame();
    int getPlayersAmount();
   // Message getAnnouncment();
}
