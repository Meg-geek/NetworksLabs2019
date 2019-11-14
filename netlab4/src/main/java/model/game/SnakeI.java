package model.game;

public interface SnakeI {
    /**
     * Moves the snake into the given direction
     * @param direction is the direction where you want to
     * move your snake
     */
    void move(Direction direction);

    /**
     * Shows if snake is alive
     * @return true if snake is alive
     */
    boolean isAlive();

    /**
     * moves the snakes to the next cell,
     * depends on snake's previous direction
     */
    void move();

    //int getLength();
}
