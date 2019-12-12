package controller;

import SnakeGameInterfaces.Controller;
import SnakeGameInterfaces.View;
import model.game.App;
import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;
import model.snakeGame.SnakeApp;
import view.SwingView;

import javax.swing.*;
import java.io.IOException;

public class SnakeGameController implements Controller {
    private View gameView;
    private String userName;
    private App snakeGameApp;

    public SnakeGameController(){
        SwingUtilities.invokeLater(()-> this.gameView = new SwingView(this));
    }

    @Override
    public void setUserName(String userName) {
        try{
            snakeGameApp = new SnakeApp(userName);
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }
        this.userName = userName;
    }

    @Override
    public void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings) {

    }

    @Override
    public void quit() {
        snakeGameApp.quit();
    }
}
