package model.game;

import java.util.List;

public interface SnakeI {
    /**
     * Moves the snake into the given direction
     * @param direction is the direction where you want to
     * move your snake
     */
    void setDirection(Direction direction);


    /**
     * moves the snakes to the next cell,
     * depends on snake's previous direction
     */
    void move();

    int getPlayerID();

    void setSnakeState(SnakeState snakeState);
    SnakeState getSnakeState();

    Direction getHeadDirection();
    List<Coordinates> getCoordinatesList();
    int getScore();
    void increaseScore(int points);
    void setDead();
    boolean isDead();
}
