package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

class GamePanel extends JPanel {
    private List<Point> snakeBodiesPointsList = Collections.emptyList();
    private List<Point> foodPointsList = Collections.emptyList();
    private List<ViewPlayerInfo> playerInfoList = Collections.emptyList();
    private final int CELL_SIDE;
    private final Color borderColor = Color.BLACK;
    private final Color foodColor = Color.RED;
    private final Color snakeBodyColor = new Color(30, 111, 23);
    private final int FIELD_WIDTH;
    private final int FIELD_HEIGHT;
    private final int POINT_WIDTH = 1;
    private final int POINT_HEIGHT = 1;
    private final int CELL_ROWS;
    private final int CELL_COLUMNS;
    private final int SPECIAL_LINE_WIDTH = 2;
    private final int POINT_PAINT_X_COEF = SnakeGameFrame.INDENT_WIDTH + SPECIAL_LINE_WIDTH;
    private final int POINT_PAINT_Y_COEF = SnakeGameFrame.INDENT_HEIGHT + SPECIAL_LINE_WIDTH;
    private JTextArea playersInfoArea;
    private final int FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/24;
    private final String TEXT_AREA_TITLE = "Список игроков";
    private int stateOrder = -1;
    private final String EXIT_BUTTON_TEXT = "Выйти из игры";
    private final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);

    GamePanel(SnakeGameFrame gameFrame, int fieldWidth, int fieldHeight){
        setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        CELL_SIDE = Math.min((SnakeGameFrame.FRAME_WIDTH/2- SnakeGameFrame.INDENT_WIDTH*2)/fieldWidth,
                (SnakeGameFrame.FRAME_HEIGHT - SnakeGameFrame.INDENT_HEIGHT*2 - 40)/fieldHeight);
        FIELD_WIDTH = CELL_SIDE * fieldWidth;
        FIELD_HEIGHT = CELL_SIDE * fieldHeight;
        CELL_ROWS = fieldHeight - 1;
        CELL_COLUMNS = fieldWidth - 1;
        setLayout(null);
        addPlayersInfo();
        addExitButton(gameFrame);
    }

    private void addExitButton(SnakeGameFrame gameFrame){
        JButton exitButton = new JButton(EXIT_BUTTON_TEXT);
        exitButton.setFont(BUTTON_FONT);
        exitButton.setBounds(SnakeGameFrame.INDENT_WIDTH + SnakeGameFrame.FRAME_WIDTH/2,
                SnakeGameFrame.FRAME_HEIGHT/2 + SnakeGameFrame.INDENT_HEIGHT*2,
                FONT_SIZE*EXIT_BUTTON_TEXT.length(), FONT_SIZE*2);
        add(exitButton);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gameFrame.goToMenu();
            }
        });
    }

    private void updateTextArea(){
        playersInfoArea.setText("");
        playersInfoArea.append(TEXT_AREA_TITLE);
        playersInfoArea.append(System.lineSeparator());
        for(ViewPlayerInfo playerInfo : playerInfoList){
            playersInfoArea.append(playerInfo.getName() + " " + playerInfo.getScore());
            if(playerInfo.isMaster()){
                playersInfoArea.append(" master");
            }
            playersInfoArea.append(System.lineSeparator());
        }
    }

    private void addPlayersInfo(){
        playersInfoArea = new JTextArea();
        playersInfoArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZE));
        playersInfoArea.setEditable(false);
        playersInfoArea.setBounds(SnakeGameFrame.INDENT_WIDTH + SnakeGameFrame.FRAME_WIDTH/2, SnakeGameFrame.INDENT_HEIGHT,
                SnakeGameFrame.FRAME_WIDTH/2 - SnakeGameFrame.INDENT_WIDTH *4 , SnakeGameFrame.FRAME_HEIGHT/2);
        playersInfoArea.append(TEXT_AREA_TITLE);
        playersInfoArea.append(System.lineSeparator());
        add(playersInfoArea);
    }

    @Override
    public void paint(Graphics graphics){
        super.paint(graphics);
        graphics.setColor(borderColor);
        graphics.drawLine(SnakeGameFrame.INDENT_WIDTH,
                SnakeGameFrame.INDENT_HEIGHT,
                FIELD_WIDTH + SnakeGameFrame.INDENT_WIDTH + SPECIAL_LINE_WIDTH,
                SnakeGameFrame.INDENT_HEIGHT);
        graphics.drawLine(SnakeGameFrame.INDENT_WIDTH,
                SnakeGameFrame.INDENT_HEIGHT,
                SnakeGameFrame.INDENT_WIDTH,
                FIELD_HEIGHT + SnakeGameFrame.INDENT_HEIGHT);
        graphics.drawLine(SnakeGameFrame.INDENT_WIDTH,
                FIELD_HEIGHT + SnakeGameFrame.INDENT_HEIGHT + SPECIAL_LINE_WIDTH,
                SnakeGameFrame.INDENT_WIDTH + FIELD_WIDTH + SPECIAL_LINE_WIDTH,
                FIELD_HEIGHT + SnakeGameFrame.INDENT_HEIGHT + SPECIAL_LINE_WIDTH);
        graphics.drawLine(SnakeGameFrame.INDENT_WIDTH + FIELD_WIDTH + SPECIAL_LINE_WIDTH,
                SnakeGameFrame.INDENT_HEIGHT,
                SnakeGameFrame.INDENT_WIDTH + FIELD_WIDTH + SPECIAL_LINE_WIDTH,
                SnakeGameFrame.INDENT_HEIGHT + FIELD_HEIGHT);
        int beginX = SnakeGameFrame.INDENT_WIDTH + CELL_SIDE;
        int beginY = SnakeGameFrame.INDENT_HEIGHT + CELL_SIDE;
        for(int i = 0; i < CELL_ROWS; i++){
            int x = beginX;
            for(int j = 0; j < CELL_COLUMNS; j++){
                graphics.drawOval(x, beginY, POINT_WIDTH,POINT_HEIGHT);
                x+=CELL_SIDE;
            }
            beginY+=CELL_SIDE;
        }
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setStroke(new BasicStroke(SPECIAL_LINE_WIDTH));
        graphics2D.setColor(snakeBodyColor);
        for(Point snakeBodyPoint : snakeBodiesPointsList){
            graphics2D.draw(new Rectangle2D.Double(snakeBodyPoint.getX()*CELL_SIDE + POINT_PAINT_X_COEF,
                    snakeBodyPoint.getY()*CELL_SIDE + POINT_PAINT_Y_COEF,
                    CELL_SIDE,
                    CELL_SIDE
                    ));
        }
        graphics2D.setColor(foodColor);
        for(Point foodPoint : foodPointsList){
            Rectangle2D apple = new Rectangle2D.Double(foodPoint.getX()*CELL_SIDE + POINT_PAINT_X_COEF,
                    foodPoint.getY()*CELL_SIDE + POINT_PAINT_Y_COEF,
                    CELL_SIDE - 1,
                    CELL_SIDE - 1
            );
            graphics2D.draw(apple);
            graphics2D.fill(apple);
        }
    }

    void updateGamePanel(int gameStateOrder,
                         java.util.List<Point> snakeCoordinatesList,
                         java.util.List<Point> foodList,
                         List<ViewPlayerInfo> players){
        if(gameStateOrder > stateOrder){
            stateOrder = gameStateOrder;
            snakeBodiesPointsList = snakeCoordinatesList;
            this.foodPointsList = foodList;
            playerInfoList = players;
        }
        updateTextArea();
        repaint();
    }
}
