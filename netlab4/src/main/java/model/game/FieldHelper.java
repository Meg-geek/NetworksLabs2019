package model.game;

public interface FieldHelper {
    Coordinates getNextCell(Coordinates cell, Direction direction);
}
