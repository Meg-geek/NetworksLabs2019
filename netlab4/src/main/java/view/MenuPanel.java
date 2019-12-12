package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

class MenuPanel extends JPanel {
    private JButton startNewGameButton;
    private JPanel gamesListPanel, buttonsPanel;
    private final int BUTTONS_AMOUNT = 2;
    private final String NEW_GAME_BUTTON_TEXT = "Начать новую игру";
    private final String EXIT_BUTTON_TEXT = "Выход";

    MenuPanel(SnakeGameFrame gameFrame){
        setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        addButtons(gameFrame);
        //addGamesList();
    }

    private void addButtons(SnakeGameFrame gameFrame){
        buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new GridLayout(BUTTONS_AMOUNT,1));
        buttonsPanel.add(createStartNewGameButton(gameFrame));
        buttonsPanel.add(createExitButton(gameFrame));
        add(buttonsPanel, BorderLayout.EAST);
    }

    private JButton createExitButton(SnakeGameFrame gameFrame){
        JButton exitButton = new JButton(EXIT_BUTTON_TEXT);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameFrame.quit();
                gameFrame.dispatchEvent(new WindowEvent(gameFrame, WindowEvent.WINDOW_CLOSING));
            }
        });
        return exitButton;
    }

    private JButton createStartNewGameButton(SnakeGameFrame gameFrame){
        JButton startNewGameButton = new JButton(NEW_GAME_BUTTON_TEXT);
        startNewGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameFrame.startNewGame();
            }
        });
        return startNewGameButton;
    }
}
