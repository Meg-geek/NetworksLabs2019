package model.snakeGame;

import model.game.Coordinates;
import model.game.Direction;
import model.game.FieldManager;
import model.game.SnakeI;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class Snake implements SnakeI {
    //head in the beggining of the queue
    private Deque<Coordinates> snakeBodyDeque = new ArrayDeque<>();
    private Direction curDirection;
    private FieldManager fieldManager;
    private boolean alive = true;

    Snake(List<Coordinates> startCoordinatesList, FieldManager fieldManager){
        for(Coordinates coord : startCoordinatesList){
            snakeBodyDeque.addLast(coord);
        }
        this.fieldManager = fieldManager;
    }

    Snake(List<Coordinates> startCoordinatesList, FieldManager fieldManager, Direction startDirection){
        this(startCoordinatesList, fieldManager);
        curDirection = startDirection;
    }

    @Override
    public void move(Direction direction) {
        switch(direction){
            case DOWN:
                if(curDirection != Direction.UP) {
                    curDirection = direction;
                }
                break;
            case UP:
                if(curDirection != Direction.DOWN) {
                    curDirection = direction;
                }
                break;
            case LEFT:
                if(curDirection != Direction.RIGHT) {
                    curDirection = direction;
                }
                break;
            case RIGHT:
                if(curDirection != Direction.LEFT) {
                    curDirection = direction;
                }
                break;
        }
        move();
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void move() {
        Coordinates nextCoord = fieldManager.getNextCell(snakeBodyDeque.getFirst(), curDirection);
        switch (nextCoord.getPointType()){
            case FOOD:
                snakeBodyDeque.addFirst(nextCoord);
                break;
            case EMPTY_CELL:
                snakeBodyDeque.addFirst(nextCoord);
                snakeBodyDeque.pollLast();
                break;
            case SNAKE_BODY:
                alive = false;
                snakeBodyDeque.clear();
                break;
            case SNAKE_TAIL:
                if(snakeBodyDeque.contains(nextCoord)){
                    alive = false;
                    snakeBodyDeque.clear();
                } else {
                    snakeBodyDeque.addFirst(nextCoord);
                    snakeBodyDeque.pollLast();
                }
                break;
        }
    }
}
