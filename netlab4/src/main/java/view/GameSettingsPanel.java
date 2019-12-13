package view;

import model.game.GameSettings;
import model.networkUtils.GameNetworkSettings;
import model.snakeGame.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static view.SnakeGameFrame.INDENT_HEIGHT;
import static view.SnakeGameFrame.INDENT_WIDTH;

class GameSettingsPanel extends JPanel implements GameSettingsPanelConstants{
   // private final int ROWS_AMOUNT = 16;
    //private final int COLUMNS_AMOUNT = 1;
    private Settings settings = new Settings();

    GameSettingsPanel(SnakeGameFrame gameFrame){
        setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        //setLayout(new GridLayout(ROWS_AMOUNT, COLUMNS_AMOUNT));
        setLayout(null);
        addTitle();
        addSliders();
        addStartGameButton(gameFrame);
    }

    private void addStartGameButton(SnakeGameFrame gameFrame){
        JButton startButton = new JButton(START_BUTTON_TEXT);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameFrame.startNewGame(settings, settings);
            }
        });
        startButton.setFont(TEXT_FONT);
        startButton.setBounds(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        add(startButton);
    }

    private void addTitle(){
        JLabel title = new JLabel(TITLE_TEXT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TITLE_FONT_SIZE));
        title.setBounds(INDENT_WIDTH, SnakeGameFrame.INDENT_HEIGHT , TITLE_WIDTH, TITLE_HEIGHT);
        add(title);
    }

    private JSlider createAndAddSlider(int minValue, int maxValue, int defaultValue, int x, int y){
        JSlider defaultSnakeSlider = new JSlider(minValue, maxValue, defaultValue);
        defaultSnakeSlider.setBackground(SnakeGameFrame.BACKGROUND_COLOR);
        defaultSnakeSlider.setMajorTickSpacing((maxValue - minValue)/MAJOR_TICKS_COEF);
       // defaultSnakeSlider.setMinorTickSpacing((maxValue - minValue)/MINOR_TICKS_COEF);
        defaultSnakeSlider.setPaintTicks(true);
        defaultSnakeSlider.setPaintLabels(true);
        defaultSnakeSlider.setBounds(x, y, SLIDER_WIDTH, SLIDER_HEIGHT);
        add(defaultSnakeSlider);
        return defaultSnakeSlider;
    }

    private void addTextLabel(String text, int x, int y){
        JLabel textLabel = new JLabel(text);
        textLabel.setBounds(x, y, FONT_SIZE*text.length(), TEXT_LABEL_HEIGHT);
        textLabel.setFont(TEXT_FONT);
        add(textLabel);
    }

    private void addSliders(){
        int y = INDENT_HEIGHT + TITLE_HEIGHT;
        addWidthSlider(y);
        y+=SLIDER_HEIGHT + INDENT_HEIGHT + TEXT_LABEL_HEIGHT;
        addHeightSlider(y);
        y+=SLIDER_HEIGHT + INDENT_HEIGHT + TEXT_LABEL_HEIGHT;
        addIndependentFoodSlider(y);
        y+=SLIDER_HEIGHT + INDENT_HEIGHT + TEXT_LABEL_HEIGHT;
        addFoodPerPlayerSlider(y);
        y = INDENT_HEIGHT + TITLE_HEIGHT;
        addDeadFoodProbSlider(y);
        y+=SLIDER_HEIGHT + INDENT_HEIGHT + TEXT_LABEL_HEIGHT;
        addStateChangeSlider(y);
        y+=SLIDER_HEIGHT + INDENT_HEIGHT + TEXT_LABEL_HEIGHT;
        addNodeTimeoutSlider(y);
        y+=SLIDER_HEIGHT + INDENT_HEIGHT + TEXT_LABEL_HEIGHT;
        addPingDelaySlider(y);
    }

    private void addNodeTimeoutSlider(int y){
        addTextLabel(NETWORK_DELAY_TIMEOUT_TITLE, RIGHT_SLIDERS_INDENT, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider nodeTimeoutSlider = createAndAddSlider(GameNetworkSettings.nodeTimeoutMSConst.getMinValue(),
                GameNetworkSettings.nodeTimeoutMSConst.getMaxValue(),
                GameNetworkSettings.nodeTimeoutMSConst.getDefaultValue(),
                RIGHT_SLIDERS_INDENT,
                y);
        nodeTimeoutSlider.addChangeListener(e -> {
            int stateDelay = ((JSlider)e.getSource()).getValue();
            settings.setStateDelayMs(stateDelay);
        });
    }

    private void addPingDelaySlider(int y){
        addTextLabel(PING_DELAY_TITLE, RIGHT_SLIDERS_INDENT, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider pingDelaySlider = createAndAddSlider(GameNetworkSettings.pingDelayMSConst.getMinValue(),
                GameNetworkSettings.pingDelayMSConst.getMaxValue(),
                GameNetworkSettings.pingDelayMSConst.getDefaultValue(),
                RIGHT_SLIDERS_INDENT,
                y);
        pingDelaySlider.addChangeListener(e -> {
            int pingDelay = ((JSlider) e.getSource()).getValue();
            settings.setPingDelayMs(pingDelay);
        });
    }

    private void addStateChangeSlider(int y){
        addTextLabel(STATE_CHANGE_TITLE, RIGHT_SLIDERS_INDENT, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider stateChangeSlider = createAndAddSlider(GameSettings.fieldStateDelayMS.getMinValue(),
                GameSettings.fieldStateDelayMS.getMaxValue(),
                GameSettings.fieldStateDelayMS.getDefaultValue(),
                RIGHT_SLIDERS_INDENT,
                y);
        stateChangeSlider.addChangeListener(e -> {
            int stateDelay = ((JSlider)e.getSource()).getValue();
            settings.setStateDelayMs(stateDelay);
        });
    }

    private void addDeadFoodProbSlider(int y){
        addTextLabel(DEAD_FOOD_PROB_TITLE, RIGHT_SLIDERS_INDENT, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider deadFoodProbslider = createAndAddSlider(Math.round(GameSettings.fieldFoodProb.getMinValue()*100),
                Math.round(GameSettings.fieldFoodProb.getMaxValue()*100),
                Math.round(GameSettings.fieldFoodProb.getDefaultValue()*100),
                RIGHT_SLIDERS_INDENT,
                y);
        deadFoodProbslider.addChangeListener(e -> {
            int foodProb = ((JSlider)e.getSource()).getValue();
            settings.setDeadFoodProb(foodProb/(float)100);
        });
    }

    private void addWidthSlider(int y){
        addTextLabel(WIDTH_TITLE, INDENT_WIDTH, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider widthSlider = createAndAddSlider(GameSettings.fieldWidth.getMinValue(),
                GameSettings.fieldWidth.getMaxValue(),
                GameSettings.fieldWidth.getDefaultValue(),
                INDENT_WIDTH,
                y);
        widthSlider.addChangeListener(e -> {
            int width = ((JSlider)e.getSource()).getValue();
            settings.setWidth(width);
        });
    }

    private void addHeightSlider(int y){
        addTextLabel(HEIGHT_TITLE, INDENT_WIDTH, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider widthSlider = createAndAddSlider(GameSettings.fieldHeight.getMinValue(),
                GameSettings.fieldHeight.getMaxValue(),
                GameSettings.fieldHeight.getDefaultValue(),
                INDENT_WIDTH,
                y);
        widthSlider.addChangeListener(e -> {
            int height = ((JSlider)e.getSource()).getValue();
            settings.setHeight(height);
        });
    }

    private void addIndependentFoodSlider(int y){
        addTextLabel(INDEPENDENT_FOOD_TITLE, INDENT_WIDTH, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider independentFoodSlider = createAndAddSlider(GameSettings.fieldFoodStatic.getMinValue(),
                GameSettings.fieldFoodStatic.getMaxValue(),
                GameSettings.fieldFoodStatic.getDefaultValue(),
                INDENT_WIDTH,
                y);
        independentFoodSlider.addChangeListener(e -> {
            int foodStatic = ((JSlider)e.getSource()).getValue();
            settings.setFoodStatic(foodStatic);
        });
    }

    private void addFoodPerPlayerSlider(int y){
        addTextLabel(FOOD_PER_PLAYER_TITLE, INDENT_WIDTH, y);
        y+=TEXT_LABEL_HEIGHT + INDENT_HEIGHT/2;
        JSlider foodPerPlayerSlider = createAndAddSlider(Math.round(GameSettings.fieldFoodPerPlayer.getMinValue()),
                Math.round(GameSettings.fieldFoodPerPlayer.getMaxValue()),
                Math.round(GameSettings.fieldFoodPerPlayer.getDefaultValue()),
                INDENT_WIDTH,
                y);

        foodPerPlayerSlider.addChangeListener(e -> {
            int foodPerPlayer = ((JSlider)e.getSource()).getValue();
            settings.setFoodPerPlayer(foodPerPlayer);
        });
    }
}
