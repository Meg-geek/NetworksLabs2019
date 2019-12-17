package view;

import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class SnakeGameFrame extends JFrame {
    private static final float SIZE_COEF = 1.5F;
    static final int FRAME_WIDTH;
    static final int FRAME_HEIGHT;
    static final int INDENT_HEIGHT;
    static final int INDENT_WIDTH;
    private static final String TITLE = "SnakeGame";
    static final Color BACKGROUND_COLOR = new Color(153, 255, 153);
    private SwingView swingView;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JPanel gameSettingsPanel;

    static {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME_WIDTH = (int)(screenDim.width/SIZE_COEF);
        FRAME_HEIGHT = (int)(screenDim.height/SIZE_COEF);
        INDENT_HEIGHT = SnakeGameFrame.FRAME_HEIGHT/65;
        INDENT_WIDTH = SnakeGameFrame.FRAME_WIDTH/50;
    }

    SnakeGameFrame(SwingView swingView){
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setTitle(TITLE);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // getContentPane().setBackground(BACKGROUND_COLOR);
        setContentPane(new HelloPanel(this));
        //getContentPane().add(new HelloPanel(), BorderLayout.CENTER);
        setVisible(true);
        this.swingView = swingView;
    }

    void setUserName(String userName){
        swingView.setUserName(userName);
        menuPanel = new MenuPanel(this);
        setContentPane(menuPanel);
        setVisible(true);
    }

    void goToMenu(){
        setContentPane(menuPanel);
        setVisible(true);
        swingView.quitGame();
    }

    void startNewGame(){
        gameSettingsPanel = new GameSettingsPanel(this);
        setContentPane(gameSettingsPanel);
        setVisible(true);
    }

    void startNewGame(GameSettings gameSettings, GameNetworkSettings networkSettings){
        swingView.startNewGame(gameSettings, networkSettings);
        gamePanel = new GamePanel(this, gameSettings.getWidth(), gameSettings.getHeight());
        setContentPane(gamePanel);
        setVisible(true);
    }

    void joinGame(GameSettings gameSettings, GameNetworkSettings networkSettings){
        gamePanel = new GamePanel(this, gameSettings.getWidth(), gameSettings.getHeight());
        setContentPane(gamePanel);
        setVisible(true);
    }

    void quit(){
        swingView.quit();
    }

    void updateGame(int gameStateOrder,
                    java.util.List<Point> snakeCoordinatesList,
                    java.util.List<Point> foodList,
                    List<ViewPlayerInfo> players){
        ((GamePanel)gamePanel).updateGamePanel(gameStateOrder, snakeCoordinatesList, foodList, players);
    }

    void joinGame(String gameInfo){
        String ip = gameInfo.substring(0, gameInfo.indexOf(" "));
        swingView.joinGame(ip);
    }

    void updateGamesList(java.util.List<ViewGameInfo> gameInfoList){
        ((MenuPanel)menuPanel).updateGamesList(gameInfoList);
    }
}