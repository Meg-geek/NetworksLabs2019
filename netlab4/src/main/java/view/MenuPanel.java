package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;

class MenuPanel extends JPanel implements MenuPanelConstants{
    private final String NEW_GAME_BUTTON_TEXT = "Начать новую игру";
    private final String EXIT_BUTTON_TEXT = "Выход";
    private final String GAMES_LIST_TITLE = "Список текущих игр";
    private final String GAMES_LIST_DESCR = "ip мастера, число игроков, характеристики поля";
    private final String START_GAME_BUTTON_TEXT = "Начать игру";
    private java.util.List<ViewGameInfo> viewGameInfoList = new ArrayList<>();
    private String selectedGame = null;
    private DefaultListModel<String> listModel;
    private JList<String> gamesList;
    private int CLEAR_TIMEOUT_MS = 7000;
    private long lastClearTime = new Date().getTime();

    MenuPanel(SnakeGameFrame gameFrame){
        setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        setLayout(null);
        addButtons(gameFrame);
        addGamesList();
    }


    private void addGamesList(){
        JLabel gamesListTitleLabel = new JLabel(GAMES_LIST_TITLE);
        JLabel gamesDescrLabel = new JLabel(GAMES_LIST_DESCR);
        gamesDescrLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, GAMES_LIST_DECR_FONT_SIZE));
        gamesListTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TITLE_FONT_SIZE));
        gamesListTitleLabel.setBounds(SnakeGameFrame.INDENT_WIDTH, SnakeGameFrame.INDENT_HEIGHT,
                TITLE_FONT_SIZE*GAMES_LIST_TITLE.length(), TITLE_FONT_SIZE*2);
        gamesDescrLabel.setBounds(SnakeGameFrame.INDENT_WIDTH, SnakeGameFrame.INDENT_HEIGHT + TITLE_FONT_SIZE,
                GAMES_LIST_DECR_FONT_SIZE*GAMES_LIST_DESCR.length(), GAMES_LIST_DECR_FONT_SIZE*2);
        add(gamesListTitleLabel);
        add(gamesDescrLabel);
        listModel = new DefaultListModel<>();
        gamesList = new JList<>(listModel);
        gamesList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                selectedGame = gamesList.getSelectedValue();
            }
        });
        gamesList.setBounds(SnakeGameFrame.INDENT_WIDTH, SnakeGameFrame.INDENT_HEIGHT*2 + TITLE_FONT_SIZE*2,
                JLIST_WIDTH, JLIST_HEIGHT);
        add(gamesList);
    }

    private void addButtons(SnakeGameFrame gameFrame){
        addStartNewGameButton(gameFrame, BUTTON_FIRST_X, BUTTON_FIRST_Y);
        addExitButton(gameFrame, BUTTON_FIRST_X,
                BUTTON_FIRST_Y + BUTTONS_INDENT + BUTTONS_FONT_SIZE*2);
        addStartGameButton(gameFrame, SnakeGameFrame.INDENT_WIDTH,
                SnakeGameFrame.INDENT_HEIGHT*3 + TITLE_FONT_SIZE*2 + JLIST_HEIGHT);
    }

    private void addExitButton(SnakeGameFrame gameFrame, int x, int y){
        JButton exitButton = new JButton(EXIT_BUTTON_TEXT);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameFrame.quit();
                gameFrame.dispatchEvent(new WindowEvent(gameFrame, WindowEvent.WINDOW_CLOSING));
            }
        });
        exitButton.setFont(buttonsFont);
        exitButton.setBounds(x, y,
                BUTTONS_WIDTH, BUTTONS_FONT_SIZE*2);
        add(exitButton);
    }

    private void addStartNewGameButton(SnakeGameFrame gameFrame, int x, int y){
        JButton startNewGameButton = new JButton(NEW_GAME_BUTTON_TEXT);
        startNewGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameFrame.startNewGame();
            }
        });
        startNewGameButton.setFont(buttonsFont);
        startNewGameButton.setBounds(x, y,
                BUTTONS_WIDTH, BUTTONS_FONT_SIZE*2);
        add(startNewGameButton);
    }

    private void addStartGameButton(SnakeGameFrame gameFrame, int x, int y){
        JButton startGameButton = new JButton(START_GAME_BUTTON_TEXT);
        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(selectedGame != null){
                    gameFrame.joinGame(selectedGame);
                }
            }
        });
        startGameButton.setFont(buttonsFont);
        startGameButton.setBounds(x, y,
                BUTTONS_WIDTH, BUTTONS_FONT_SIZE*2);
        add(startGameButton);
    }

    void updateGamesList(java.util.List<ViewGameInfo> gameInfoList){
        viewGameInfoList = gameInfoList;
        if(new Date().getTime() - lastClearTime > CLEAR_TIMEOUT_MS){
            listModel.clear();
            lastClearTime = new Date().getTime();
        }
        for(ViewGameInfo gameInfo : viewGameInfoList){
            if(!listModel.contains(gameInfo.toString())){
                listModel.add(listModel.getSize(), gameInfo.toString());
            }
        }
        gamesList.setModel(listModel);
    }
}
