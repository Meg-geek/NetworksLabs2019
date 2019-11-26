package model.networkUtils;

import model.game.GameSettings;

public interface NetworkGame {
    void handleMessage(Message message);
    void quitGame();
    long getAndIncrementMsgSeq();
    int getMyID();
   // List<NetworkUser> getUsersList();
    GameNetworkSettings getNetworkSettings();
    GameSettings getGameSettings();
    boolean equals(NetworkGame game);
    String getMasterIP();
}
