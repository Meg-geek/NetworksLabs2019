package model.game;

import model.snakeGameNetwork.messages.GameStateMessage;

public interface GameField {
    void moveSnakes();
    void changeState(GameStateMessage gameStateMessage);
    //void setSnakeDirection(int snakeID, Direction direction);
    GameState getState();
    boolean addPlayer(SnakeGamePlayerI player);
    boolean isJoinable();
    void setSnakeDirection(int playerID, Direction direction);
    void removePlayer(int playerId);
}
