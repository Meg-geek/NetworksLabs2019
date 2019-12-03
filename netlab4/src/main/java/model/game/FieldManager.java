package model.game;

import java.util.List;

public interface FieldManager {
    int FIRST_COORD = 0;
    Coordinates getNextCell(Coordinates cell, Direction direction);
    /*
    returns coordinates for snake or null if there's no free space for the new snake
     */
    List<Coordinates> getNewSnakeCoords(int playerID);
   // void moveSnakes();
}
