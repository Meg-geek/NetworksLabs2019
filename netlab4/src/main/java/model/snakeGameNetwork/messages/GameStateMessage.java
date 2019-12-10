package model.snakeGameNetwork.messages;

import model.game.Coordinates;
import model.game.GameSettings;
import model.game.SnakeGamePlayerI;
import model.game.SnakeI;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.GameNetworkSettings;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

import java.util.List;

public class GameStateMessage extends Message {
    private List<Coordinates> foodCoordsList;
    private int stateOrder;
    private List <SnakeGamePlayerI> gamersList;
    private List<SnakeI> snakesList;
    private GameSettings gameSettings;
    private GameNetworkSettings gameNetworkSettings;

    public GameStateMessage(long msgSeq, int senderID, int stateOrder, List<SnakeI> snakesList, List<Coordinates> foodList,
                            List<SnakeGamePlayerI> playersList, GameSettings settings, GameNetworkSettings networkSettings){
        super(new BasicMessageInfo(msgSeq, senderID));
        this.stateOrder = stateOrder;
        this.snakesList = snakesList;
        this.foodCoordsList = foodList;
        this.gamersList = playersList;
        this.gameSettings = settings;
        this.gameNetworkSettings = networkSettings;
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
}
