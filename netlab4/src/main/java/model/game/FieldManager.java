package model.game;

public interface FieldManager {
    int FIRST_COORD = 1;
    Coordinates getNextCell(Coordinates cell, Direction direction);
}
