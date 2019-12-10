package model.game;

import java.util.List;

public interface FieldManager {
    int FIRST_COORD = 0;
    //for SnakeFieldHelper ?
    //
    /*
    returns coordinates for snake or null if there's no free space for the new snake
     */
    List<Coordinates> getNewSnakeCoords(int playerID);
    boolean isJoinable();
    List<Coordinates> getFoodList();
    void setFoodList(List<Coordinates> foodList);
   // void setFood();
   // void moveSnakes();
}
