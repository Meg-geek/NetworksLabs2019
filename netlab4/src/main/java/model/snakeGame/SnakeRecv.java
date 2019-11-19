package model.snakeGame;

import model.game.Coordinates;
import model.game.Direction;
import model.game.SnakeI;

import java.util.List;

/*
* snake for normal node, has info for interface*/
public class SnakeRecv implements SnakeI {
    private List<Coordinates> bodyCoordList;
    private Direction direction;
    private int playerID;

    public SnakeRecv(List<Coordinates> coordinatesList, Direction direction,
                     int playerID){
        bodyCoordList = coordinatesList;
        this.direction = direction;
        this.playerID = playerID;
    }

    @Override
    public void setDirection(Direction direction) {

    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void move() {

    }
}
