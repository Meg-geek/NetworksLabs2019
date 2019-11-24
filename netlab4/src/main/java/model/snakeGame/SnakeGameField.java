package model.snakeGame;

import model.game.Direction;
import model.game.GameField;
import model.game.SnakeI;
import model.snakeGameNetwork.messages.GameStateMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnakeGameField implements GameField {
    //private GameState gameState;
    //private List<Player> playersList;
    //private List<SnakeI> snakesList;
    private Map<Integer, SnakeI> IDSnakeMap = new ConcurrentHashMap<>();

    @Override
    public void moveSnakes() {
        for(Map.Entry<Integer, SnakeI> snakeIEntry : IDSnakeMap.entrySet()){
            snakeIEntry.getValue().move();
        }
    }

    @Override
    public void changeState(GameStateMessage gameState) {

    }


    @Override
    public void setSnakeDirection(int snakeID, Direction direction){
        SnakeI snake = IDSnakeMap.get(snakeID);
        if(snake != null){
            snake.setDirection(direction);
        }
    }

    //public void snakeDead(int snakeID){}
}
