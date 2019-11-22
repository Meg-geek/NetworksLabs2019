package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class ACKMessage extends Message {
    public ACKMessage(BasicMessageInfo messageInfo){
        super(messageInfo);
    }

    @Override
    public MessageType getType() {
        return MessageType.ACK;
    }
}
