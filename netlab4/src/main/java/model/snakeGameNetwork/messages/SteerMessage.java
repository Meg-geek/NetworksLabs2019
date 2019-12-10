package model.snakeGameNetwork.messages;

import model.game.Direction;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class SteerMessage extends Message {
    private model.game.Direction direction;

    public SteerMessage(long messageNumber, int senderId, int reciverId, Direction direction){
        super(new BasicMessageInfo(messageNumber, senderId, reciverId));
        this.direction = direction;
    }

    @Override
    public MessageType getType() {
        return MessageType.STEER;
    }

    public Direction getDirection(){
        return direction;
    }
}
