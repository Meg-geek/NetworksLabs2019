package model.snakeGame;

import model.game.*;
import model.snakeGameNetwork.messages.GameStateMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SnakeGameField implements GameField {
    private Map<Integer, SnakeI> IDSnakeMap = new ConcurrentHashMap<>();
    private AtomicInteger gameStateOrder;
    private FieldManager fieldManager;
    private FieldHelper fieldHelper;
    private List<SnakeGamePlayerI> playersList;
    private List<SnakeI> snakesList;

    //если мы создаем игру
    SnakeGameField(GameSettings settings, int id, SnakeGamePlayerI myPlayer){
        this(settings);
        //поставили змейку
        SnakeI mySnake = new Snake(fieldManager.getNewSnakeCoords(id),  id, fieldHelper);
        IDSnakeMap.put(id, mySnake);
        playersList = new ArrayList<>();
        playersList.add(myPlayer);
    }

    SnakeGameField(GameSettings settings){
        SnakeGameFieldManager snakeGameFieldManager = new SnakeGameFieldManager(settings);
        fieldManager = snakeGameFieldManager;
        fieldHelper = snakeGameFieldManager;
        gameStateOrder = new AtomicInteger();
    }

    SnakeGameField(GameSettings settings, GameState state){
        this(settings);
        gameStateOrder.set(state.getStateOrder());
        this.playersList = state.getPlayersList();
        fieldManager.setFoodList(state.getFoodList());
        this.snakesList = state.getSnakesList();
    }


    @Override
    public void moveSnakes() {
        for(Map.Entry<Integer, SnakeI> snakeIEntry : IDSnakeMap.entrySet()){
            snakeIEntry.getValue().move();
        }
        fieldManager.checkSnakes();
        gameStateOrder.incrementAndGet();
    }

    @Override
    public void changeState(GameStateMessage gameStateMessage) {
        if(gameStateOrder.get() < gameStateMessage.getStateOrder()){
            gameStateOrder.set(gameStateMessage.getStateOrder());
            this.playersList = gameStateMessage.getSnakeGamePlayersList();
            fieldManager.setFoodList(gameStateMessage.getFoodCoordsList());
            this.snakesList = gameStateMessage.getSnakesList();
        }
    }

/*
    @Override
    public void setSnakeDirection(int snakeID, Direction direction){
        SnakeI snake = IDSnakeMap.get(snakeID);
        if(snake != null){
            snake.setDirection(direction);
        }
    }

 */

    @Override
    public GameState getState() {
        return new SnakeGameState(gameStateOrder.get(), new ArrayList<>(IDSnakeMap.values()),
                fieldManager.getFoodList(), playersList);
    }

    @Override
    public boolean addPlayer(SnakeGamePlayerI player) {
        List<Coordinates> snakeCoordinatesList = fieldManager.getNewSnakeCoords(player.getID());
        if(snakeCoordinatesList == null){
            return false;
        }
        SnakeI playerSnake = new Snake(snakeCoordinatesList, player.getID(), fieldHelper);
        IDSnakeMap.put(playerSnake.getPlayerID(), playerSnake);
        playersList.add(player);
        return true;
    }

    @Override
    public boolean isJoinable() {
        return fieldManager.isJoinable();
    }

    @Override
    public void setSnakeDirection(int playerID, Direction direction) {
        SnakeI snake = IDSnakeMap.get(playerID);
        if(snake != null){
            snake.setDirection(direction);
        }
    }

    @Override
    public void removePlayer(int playerId) {
        SnakeGamePlayerI player = null;
        for(SnakeGamePlayerI playerI : playersList){
            if(playerI.getID() == playerId){
                player = playerI;
                break;
            }
        }
        if(player != null){
            playersList.remove(player);
        }
        SnakeI snake = IDSnakeMap.get(playerId);
        if(snake != null){
            snake.setSnakeState(SnakeState.ZOMBIE);
        }
    }
}

class SnakeGameState implements model.game.GameState {
    private int stateOrder;
    private List<SnakeI> snakesList;
    private List<Coordinates> foodList;
    private List<SnakeGamePlayerI> playersList;

    SnakeGameState(int stateOrder, List<SnakeI> snakesList, List<Coordinates> foodList,
                   List<SnakeGamePlayerI> playersList){
        this.stateOrder = stateOrder;
        this.snakesList = snakesList;
        this.foodList = foodList;
        this.playersList = playersList;
    }

    @Override
    public int getStateOrder() {
        return stateOrder;
    }

    @Override
    public List<SnakeI> getSnakesList() {
        return snakesList;
    }

    @Override
    public List<SnakeGamePlayerI> getPlayersList() {
        return playersList;
    }

    @Override
    public List<Coordinates> getFoodList() {
        return foodList;
    }
}