package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class HelloPanel extends JPanel {
    private JTextField nameField;
    private JLabel textLabel;//, helloTextLabel;
    private JButton startGameButton;
    private static final int COLUMNS = 50;
    private static final String FIELD_DESCR = "Введите свое имя";
    private static final String BUTTON_TEXT = "Начать";
   // private static final String HELLO_TEXT = "Добро пожаловать в сетевую игру змейка!";
    //private static final int COLUMNS_AMOUNT = 1;
    //private static final int ROWS_AMOUNT = 3;

    HelloPanel(SnakeGameFrame gameFrame){
        textLabel = new JLabel(FIELD_DESCR);
       // helloTextLabel = new JLabel(HELLO_TEXT);
        nameField = new JTextField(COLUMNS);
        startGameButton  = new JButton(BUTTON_TEXT);
        setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        //setLayout(new GridLayout(ROWS_AMOUNT, COLUMNS_AMOUNT));
       // add(helloTextLabel);
        add(textLabel, BorderLayout.NORTH);
        add(nameField, BorderLayout.CENTER);
        add(startGameButton, BorderLayout.CENTER);
        setButtonSettings(gameFrame);
    }

    private void setButtonSettings(SnakeGameFrame gameFrame){
        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                String name = nameField.getText();
                if(name.length() > 0){
                    gameFrame.setUserName(name);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }
        });
    }
}
