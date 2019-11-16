package model.snakeGame;

import model.game.GameField;

public class MovementThread implements Runnable{
   private GameField gameField;

    MovementThread(GameField gameField){
        this.gameField = gameField;
    }

    @Override
    public void run() {
        gameField.moveSnakes();
    }
}
