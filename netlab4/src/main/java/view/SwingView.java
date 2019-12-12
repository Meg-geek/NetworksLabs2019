package view;

import SnakeGameInterfaces.Controller;
import SnakeGameInterfaces.View;
import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;

import javax.swing.*;

public class SwingView implements View {
    private JFrame gameFrame;
    private Controller gameController;

    public SwingView(Controller gameController){
        gameFrame = new SnakeGameFrame(this);
        this.gameController = gameController;
    }

    void setUserName(String userName){
        gameController.setUserName(userName);
    }

    void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings){

    }

    void quit(){
        gameController.quit();
    }
}
