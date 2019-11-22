package model.snakeGameNetwork.messages;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Direction;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class SteerMessage extends Message {
    private model.game.Direction direction;

    public SteerMessage(BasicMessageInfo messageInfo, SnakesProto.Direction direction){
        super(messageInfo);
        setDirection(direction);
    }

    private void setDirection(SnakesProto.Direction protoDirection){
        switch (protoDirection){
            case LEFT:
                direction = Direction.LEFT;
            case RIGHT:
                direction = Direction.RIGHT;
            case UP:
                direction = Direction.UP;
            case DOWN:
                direction = Direction.DOWN;
        }
    }

    @Override
    public MessageType getType() {
        return MessageType.STEER;
    }

    public Direction getDirection(){
        return direction;
    }
}
