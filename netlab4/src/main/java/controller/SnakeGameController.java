package controller;

import SnakeGameInterfaces.Controller;
import SnakeGameInterfaces.View;
import model.game.*;
import model.networkUtils.GameNetworkSettings;
import model.networkUtils.NetworkGame;
import model.networkUtils.NodeRole;
import model.snakeGame.SnakeApp;
import view.SwingView;
import view.ViewGameInfo;
import view.ViewPlayerInfo;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SnakeGameController implements Controller {
    private View gameView;
    private String userName;
    private App snakeGameApp;

    public SnakeGameController(){
        //SwingUtilities.invokeLater(()-> this.gameView = new SwingView(this));
        this.gameView = new SwingView(this);
    }

    @Override
    public void setUserName(String userName) {
        try{
            snakeGameApp = new SnakeApp(userName, this);
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }
        this.userName = userName;
    }

    @Override
    public void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings) {
        snakeGameApp.createGame(gameSettings, networkSettings);
    }

    @Override
    public void quit() {
        snakeGameApp.quit();
    }

    @Override
    public void updateGameState(GameState gameState) {
        List<Point> snakesPointList = new ArrayList<>();
        for(SnakeI snakeI : gameState.getSnakesList()){
            for(Coordinates coordinates : snakeI.getCoordinatesList()){
                snakesPointList.add(new Point(coordinates.getX(), coordinates.getY()));
            }
        }
        List<Point> foodList = new ArrayList<>();
        for(Coordinates coordinates : gameState.getFoodList()){
            foodList.add(new Point(coordinates.getX(), coordinates.getY()));
        }
        List<ViewPlayerInfo> playerInfoList = new ArrayList<>();
        for(SnakeGamePlayerI snakeGamePlayerI : gameState.getPlayersList()){
            playerInfoList.add(new ViewPlayerInfo(snakeGamePlayerI.getName(),
                    snakeGamePlayerI.getScore(),
                    snakeGamePlayerI.getRole() == NodeRole.MASTER));
        }
        gameView.updateGame(gameState.getStateOrder(), snakesPointList, foodList, playerInfoList);
    }

    @Override
    public void directionKeyPressed(KeyEvent pressedButton){
        int keyPressed = pressedButton.getKeyCode();
        switch (keyPressed){
            case KeyEvent.VK_UP:
                snakeGameApp.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                snakeGameApp.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                snakeGameApp.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                snakeGameApp.setDirection(Direction.RIGHT);
                break;
            default:
                break;
        }
    }

    @Override
    public void quitGame() {
        snakeGameApp.quitGame();
    }

    @Override
    public void updateGamesList(List<NetworkGame> gamesList) {
        List<ViewGameInfo> gameInfoList = new ArrayList<>();
        for(NetworkGame networkGame : gamesList){
            gameInfoList.add(new ViewGameInfo(networkGame.getMaster().getIP(),
                    networkGame.getPlayersAmount(),
                    networkGame.getGameSettings().getWidth(),
                    networkGame.getGameSettings().getHeight()));
        }
        gameView.updateGamesList(gameInfoList);
    }

    @Override
    public void joinGame(String ip) {
        snakeGameApp.joinGame(ip);
    }

    @Override
    public void showError(String errorMessage) {
        gameView.showError(errorMessage);
    }
}
