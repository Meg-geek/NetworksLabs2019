package view;

import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
                e.getWindow().dispose();
                System.exit(0);
            }
        });
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
        gamePanel = new GamePanel(this, gameSettings.getWidth(), gameSettings.getHeight());
        setContentPane(gamePanel);
        setVisible(true);
        swingView.startNewGame(gameSettings, networkSettings);
    }
/*
    void joinGame(GameSettings gameSettings){
        gamePanel = new GamePanel(this, gameSettings.getWidth(), gameSettings.getHeight());
        setContentPane(gamePanel);
        setVisible(true);
    }

 */

    void quit(){
        if(swingView != null){
            swingView.quit();
        }
    }

    void updateGame(int gameStateOrder,
                    java.util.List<Point> snakeCoordinatesList,
                    java.util.List<Point> foodList,
                    List<ViewPlayerInfo> players){
        ((GamePanel)gamePanel).updateGamePanel(gameStateOrder, snakeCoordinatesList, foodList, players);
    }

    void joinGame(String gameInfo){
        String ip = gameInfo.substring(0, gameInfo.indexOf(" "));
        String[] splitedInfo = gameInfo.split(" ");
        int width = Integer.parseInt(splitedInfo[2]);
        int height = Integer.parseInt(splitedInfo[4]);
        gamePanel = new GamePanel(this, width, height);
        setContentPane(gamePanel);
        setVisible(true);
        swingView.joinGame(ip);
    }

    void updateGamesList(java.util.List<ViewGameInfo> gameInfoList){
        ((MenuPanel)menuPanel).updateGamesList(gameInfoList);
    }
}