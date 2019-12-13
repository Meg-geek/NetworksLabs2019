package view;

import java.awt.*;

import static view.SnakeGameFrame.INDENT_HEIGHT;
import static view.SnakeGameFrame.INDENT_WIDTH;

public interface GameSettingsPanelConstants{
    String TITLE_TEXT = "Настройки игры";
    int FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/26;
    int TITLE_FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/22;
    String WIDTH_TITLE = "Ширина поля";
    String HEIGHT_TITLE = "Высота поля";
    String INDEPENDENT_FOOD_TITLE = "Постоянное число еды";
    String FOOD_PER_PLAYER_TITLE = "Еды на каждого игрока";
    String STATE_CHANGE_TITLE = "Смена состояний через";
    String DEAD_FOOD_PROB_TITLE = "Вероятность превращения " + System.lineSeparator() + "мертвой клетки в еду";
    String PING_DELAY_TITLE = "Задержка между отправкой ping";
    String NETWORK_DELAY_TIMEOUT_TITLE = "Сетевой таймаут";
    int TITLE_HEIGHT = TITLE_FONT_SIZE*2;
    int TITLE_WIDTH = TITLE_FONT_SIZE*TITLE_TEXT.length();
    int MAJOR_TICKS_COEF = 5;
    int SLIDER_WIDTH = SnakeGameFrame.FRAME_WIDTH/4;
    int SLIDER_HEIGHT = TITLE_HEIGHT;
    int TEXT_LABEL_HEIGHT = FONT_SIZE*2;
    Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
    int RIGHT_SLIDERS_INDENT = SLIDER_WIDTH + INDENT_WIDTH*4;
    String START_BUTTON_TEXT = "Начать игру";
    int BUTTON_X = INDENT_WIDTH + SLIDER_WIDTH/2;
    int BUTTON_Y = SLIDER_HEIGHT*4 + TITLE_HEIGHT*4 + INDENT_HEIGHT*2 + TITLE_HEIGHT;
    int BUTTON_WIDTH = FONT_SIZE*START_BUTTON_TEXT.length();
    int BUTTON_HEIGHT = FONT_SIZE*2;
}
