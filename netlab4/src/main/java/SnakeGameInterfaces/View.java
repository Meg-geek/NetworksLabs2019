package SnakeGameInterfaces;

import view.ViewGameInfo;
import view.ViewPlayerInfo;

import java.awt.*;
import java.util.List;

public interface View {
    void updateGame(int gameStateOrder,
                    List<Point> snakeCoordinatesList,
                    List<Point> foodList,
                    List<ViewPlayerInfo> players);
    void updateGamesList(List<ViewGameInfo> gamesList);
    void joinGame(String masterIp);
    void showError(String errorText);
}
