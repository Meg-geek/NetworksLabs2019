package model.game;

import model.snakeGameNetwork.messages.GameStateMessage;

public interface GameField {
    void moveSnakes();
    void changeState(GameStateMessage gameState);
    void setSnakeDirection(int snakeID, Direction direction);
}
