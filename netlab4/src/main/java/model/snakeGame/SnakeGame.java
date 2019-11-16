package model.snakeGame;

import model.game.Game;
import model.game.GameField;
import model.game.GameSettings;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnakeGame implements Game {
    private final int THREADS_AMOUNT = 1;
    private final int INIT_DELAY = 0;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private GameSettings gameSettings;
    private GameField gameField;

    SnakeGame(GameSettings gameSettings){
        this.gameSettings = gameSettings;
    }

    @Override
    public void start() {
       scheduledThreadPool.scheduleWithFixedDelay(new MovementThread(gameField),
               INIT_DELAY,
               gameSettings.getStateDelayMS(),
               TimeUnit.MILLISECONDS);
    }

}
