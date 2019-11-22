package model.networkUtils;

public abstract class Message {
    private BasicMessageInfo messageInfo;

    public Message(){}

    public Message(BasicMessageInfo messageInfo){
        this.messageInfo = messageInfo;
    }

    final public long getNumber(){
        return messageInfo.getNumber();
    }

    final public int getSenderID(){
        return messageInfo.getSenderID();
    }

    final public int getReceiverID(){
        return messageInfo.getReceiverID();
    }

    abstract public MessageType getType();
}
