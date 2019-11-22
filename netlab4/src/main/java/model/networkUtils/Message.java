package model.networkUtils;

public abstract class Message {
    private long messageNumber;
    private int recieverID, senderID;

    public Message(){}

    public Message(long messageNumber, int senderID, int recieverID){
        this.messageNumber = messageNumber;
        this.senderID = senderID;
        this.recieverID = recieverID;
    }

    final public long getNumber(){
        return messageNumber;
    }

    final public int getSenderID(){
        return senderID;
    }

    final public int getReceiverID(){
        return recieverID;
    }

    abstract public MessageType getType();
}
