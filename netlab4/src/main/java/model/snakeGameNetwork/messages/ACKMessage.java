package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class ACKMessage extends Message {
    public ACKMessage(long msgSeq, int senderId, int recieverId){
        super(new BasicMessageInfo(msgSeq, senderId, recieverId));
    }

    public ACKMessage(long msgSeq){
        super(new BasicMessageInfo(msgSeq));
    }

    @Override
    public MessageType getType() {
        return MessageType.ACK;
    }
}
