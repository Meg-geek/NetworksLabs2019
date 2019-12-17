package view;

import SnakeGameInterfaces.Controller;
import SnakeGameInterfaces.View;
import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class SwingView implements View {
    private JFrame gameFrame;
    private Controller gameController;

    public SwingView(Controller gameController){
        gameFrame = new SnakeGameFrame(this);
        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameController.directionKeyPressed(e);
            }
        });
        gameFrame.setFocusable(true);
        this.gameController = gameController;
    }

    void setUserName(String userName){
        gameController.setUserName(userName);
    }

    void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings){
        gameController.startNewGame(gameSettings, networkSettings);
    }

    void quit(){
        gameController.quit();
    }

    @Override
    public void updateGame(int gameStateOrder,
                           List<Point> snakeCoordinatesList,
                           List<Point> foodList,
                           List<ViewPlayerInfo> players) {
        ((SnakeGameFrame)gameFrame).updateGame(gameStateOrder, snakeCoordinatesList, foodList, players);
    }

    @Override
    public void updateGamesList(List<ViewGameInfo> gamesList) {
        ((SnakeGameFrame)gameFrame).updateGamesList(gamesList);
    }

    @Override
    public void joinGame(String masterIp) {
        gameController.joinGame(masterIp);
    }

    @Override
    public void showError(String errorText) {

    }

    @Override
    public void joinGame(GameSettings gameSettings, GameNetworkSettings networkSettings) {
        ((SnakeGameFrame)gameFrame).joinGame(gameSettings, networkSettings);
    }

    void quitGame(){
        gameController.quitGame();
    }
}
