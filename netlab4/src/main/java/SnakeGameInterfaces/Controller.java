package SnakeGameInterfaces;

import model.game.GameSettings;
import model.game.GameState;
import model.networkUtils.GameNetworkSettings;
import model.networkUtils.NetworkGame;

import java.awt.event.KeyEvent;
import java.util.List;

public interface Controller {
    void setUserName(String userName);
    void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings);
    void quit();
    void updateGameState(GameState gameState);
    void directionKeyPressed(KeyEvent pressedButton);
    void quitGame();
    void updateGamesList(List<NetworkGame> gamesList);
    void joinGame(String ip);
    void showError(String errorMessage);
    void joinGame(GameSettings gameSettings, GameNetworkSettings networkSettings);
}
