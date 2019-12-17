package model.snakeGame;

import model.game.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class Snake implements SnakeI {
    //head in the beggining of the queue
    private Deque<Coordinates> snakeBodyDeque = new ArrayDeque<>();
    private Direction curDirection;
    private FieldHelper fieldHelper = null;
    private int playerID;
    private SnakeState snakeState;
    private int score = 0;

    public Snake(List<Coordinates> startCoordinates, int playerID){
        for(Coordinates coord : startCoordinates){
            snakeBodyDeque.addLast(coord);
        }
        curDirection = getDirection(startCoordinates);
        this.playerID = playerID;
        this.snakeState = SnakeState.ALIVE;
    }

    public Snake(List<Coordinates> startCoordinates, int playerID, FieldHelper fieldHelper){
        this(startCoordinates, playerID);
        this.fieldHelper = fieldHelper;
    }

    private Direction getDirection(List<Coordinates> snakeBody){
        if(snakeBody.size() < 2){
            return Direction.DOWN;
        }
        int x = snakeBody.get(0).getX() - snakeBody.get(1).getX();
        int y = snakeBody.get(0).getY() - snakeBody.get(1).getY();
        if(x == 0){
            if(y < 0){
                return Direction.UP;
            }
            return Direction.DOWN;
        }
        if(x < 0){
            return Direction.LEFT;
        }
        return Direction.RIGHT;
    }

    @Override
    public void setDirection(Direction direction) {
        Direction realDirection = getDirection(new ArrayList<>(snakeBodyDeque));
        switch(direction){
            case DOWN:
                if(realDirection != Direction.UP) {
                    curDirection = direction;
                }
                break;
            case UP:
                if(realDirection != Direction.DOWN) {
                    curDirection = direction;
                }
                break;
            case LEFT:
                if(realDirection != Direction.RIGHT) {
                    curDirection = direction;
                }
                break;
            case RIGHT:
                if(realDirection != Direction.LEFT) {
                    curDirection = direction;
                }
                break;
        }
    }

    @Override
    public void move() {
        if(fieldHelper == null){
            //for debug
            System.out.println("Illegal call to fieldHelper");
            return;
        }
        Coordinates nextCoord = fieldHelper.getNextCell(snakeBodyDeque.getFirst(), curDirection, playerID);
        switch (nextCoord.getPointType()){
            case FOOD:
                snakeBodyDeque.addFirst(nextCoord);
                break;
            case EMPTY_CELL:
                snakeBodyDeque.addFirst(nextCoord);
                snakeBodyDeque.pollLast();
                break;
            case SNAKE_BODY:
                snakeBodyDeque.clear();
                break;
            case SNAKE_TAIL:
                if(snakeBodyDeque.contains(nextCoord)){
                    snakeBodyDeque.clear();
                } else {
                    snakeBodyDeque.addFirst(nextCoord);
                    snakeBodyDeque.pollLast();
                }
                break;
        }
    }

    @Override
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public void setSnakeState(SnakeState snakeState) {
        this.snakeState = snakeState;
    }

    @Override
    public SnakeState getSnakeState() {
        return snakeState;
    }

    @Override
    public Direction getHeadDirection() {
        return curDirection;
    }

    @Override
    public List<Coordinates> getCoordinatesList() {
        return new ArrayList<>(snakeBodyDeque);
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void increaseScore(int points) {
        if(points > 0){
            score+=points;
        }
    }

    @Override
    public void setDead() {
        snakeBodyDeque.clear();
        score = 0;
    }

    @Override
    public boolean isDead() {
        return (snakeBodyDeque.size()==0);
    }
}
