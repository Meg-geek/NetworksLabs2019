package model.game;

import model.networkUtils.GameNetworkSettings;

public interface App {
   // void start();
    void createGame(GameSettings gameSettings, GameNetworkSettings networkSettings);
    void quit();
    void setDirection(Direction direction);
    void quitGame();
    void joinGame(String ip);
}
