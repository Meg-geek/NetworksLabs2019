package model.game;

import java.util.List;

public interface GameState {
    int getStateOrder();
    List<SnakeI> getSnakesList();
    List<SnakeGamePlayerI> getPlayersList();
    List<Coordinates> getFoodList();
    int getMaxID();
}
