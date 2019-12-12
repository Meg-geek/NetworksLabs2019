package model.game;

import model.networkUtils.GameNetworkSettings;

public interface App {
   // void start();
    void createGame(GameSettings gameSettings, GameNetworkSettings networkSettings);
    void setPlayerName(String name);
    void quit();
}
