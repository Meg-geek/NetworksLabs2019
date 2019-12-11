package model.snakeGameNetwork.messages;

import model.game.Coordinates;
import model.game.SnakeGamePlayerI;
import model.game.SnakeI;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;
import model.snakeGame.Settings;

import java.util.List;

public class GameStateMessage extends Message {
    private List<Coordinates> foodCoordsList;
    private int stateOrder;
    private List <SnakeGamePlayerI> gamersList;
    private List<SnakeI> snakesList;
    private Settings gameSettings;

    public GameStateMessage(long msgSeq, int senderID, int stateOrder, List<SnakeI> snakesList, List<Coordinates> foodList,
                            List<SnakeGamePlayerI> playersList, Settings gameSettings){
        super(new BasicMessageInfo(msgSeq, senderID));
        this.stateOrder = stateOrder;
        this.snakesList = snakesList;
        this.foodCoordsList = foodList;
        this.gamersList = playersList;
        this.gameSettings = gameSettings;
    }

    public List<Coordinates> getFoodCoordsList(){
        return foodCoordsList;
    }

    public List<SnakeI> getSnakesList(){
        return snakesList;
    }

    public int getStateOrder(){
        return stateOrder;
    }

    @Override
    public MessageType getType() {
        return MessageType.STATE;
    }

    public List<SnakeGamePlayerI> getSnakeGamePlayersList(){
        return gamersList;
    }

    public Settings getGameSettings() {
        return gameSettings;
    }
}
