package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class ErrorMessage extends Message {
    private String errorMessage;

    public ErrorMessage(long msgSeq, int senderId, int recieverId, String errorMessage){
        super(new BasicMessageInfo(msgSeq, senderId, recieverId));
        this.errorMessage = errorMessage;
    }

    public ErrorMessage(long msgSeq, String errorMessage){
        super(new BasicMessageInfo(msgSeq));
        this.errorMessage = errorMessage;
    }

    @Override
    public MessageType getType() {
        return MessageType.ERROR;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
