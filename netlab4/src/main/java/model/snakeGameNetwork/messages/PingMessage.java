package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class PingMessage extends Message {
    public PingMessage(long msgSeq, int senderId, int recieverId){
        super(new BasicMessageInfo(msgSeq, senderId, recieverId));
    }

    @Override
    public MessageType getType() {
        return MessageType.PING;
    }
}
