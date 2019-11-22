package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class ErrorMessage extends Message {
    private String errorMessage;

    public ErrorMessage(BasicMessageInfo messageInfo, String errorMessage){
        super(messageInfo);
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
