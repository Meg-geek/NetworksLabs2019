package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class HelloPanel extends JPanel {
    private JTextField nameField;
    private JLabel textLabel, helloTextLabel;
    private JButton startGameButton;
    private static final int COLUMNS = 50;
    private static final String FIELD_DESCR = "Введите свое имя";
    private static final String BUTTON_TEXT = "Начать";
    private static final String HELLO_TEXT = "Добро пожаловать в сетевую игру змейка!";
    //private static final int COLUMNS_AMOUNT = 1;
    //private static final int ROWS_AMOUNT = 3;
    private final int HELLO_FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/20;
    private final int TEXT_FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/22;

    HelloPanel(SnakeGameFrame gameFrame){
        textLabel = new JLabel(FIELD_DESCR);
        textLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TEXT_FONT_SIZE));
        helloTextLabel = new JLabel(HELLO_TEXT);
        helloTextLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, HELLO_FONT_SIZE));
        nameField = new JTextField(COLUMNS);
        startGameButton  = new JButton(BUTTON_TEXT);
        setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        //setLayout(new GridLayout(ROWS_AMOUNT, COLUMNS_AMOUNT));
        JPanel textPanel = new JPanel(){
            {
                setBackground(SnakeGameFrame.BACKGROUND_COLOR);
            }
        };
        textPanel.setLayout(new GridLayout(2,1));
        textPanel.add(helloTextLabel);
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);
        add(nameField, BorderLayout.SOUTH);
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
