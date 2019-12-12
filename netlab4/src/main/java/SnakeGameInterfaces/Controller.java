package SnakeGameInterfaces;

import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;

public interface Controller {
    void setUserName(String userName);
    void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings);
    void quit();
}
