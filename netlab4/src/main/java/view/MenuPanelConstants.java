package view;

import java.awt.*;

public interface MenuPanelConstants {
    int BUTTONS_FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/26;
    int TITLE_FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/28;
    int GAMES_LIST_DECR_FONT_SIZE = SnakeGameFrame.FRAME_HEIGHT/34;
    Font buttonsFont = new Font(Font.SANS_SERIF, Font.BOLD, BUTTONS_FONT_SIZE);
    int BUTTON_FIRST_X = SnakeGameFrame.FRAME_WIDTH/2;
    int BUTTON_FIRST_Y = SnakeGameFrame.INDENT_HEIGHT*3;
    int BUTTONS_INDENT = SnakeGameFrame.INDENT_HEIGHT*2;
    int BUTTONS_WIDTH = BUTTONS_FONT_SIZE*20;
    int JLIST_WIDTH = SnakeGameFrame.FRAME_WIDTH/2 - SnakeGameFrame.INDENT_WIDTH*4;
    int JLIST_HEIGHT = SnakeGameFrame.FRAME_HEIGHT/2;
}
