package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class PingMessage extends Message {
    public PingMessage(BasicMessageInfo messageInfo){
        super(messageInfo);
    }

    @Override
    public MessageType getType() {
        return MessageType.PING;
    }
}
