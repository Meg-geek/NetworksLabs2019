package model.networkUtils;

public class BasicMessageInfo {
    private long messageNumber;
    private int recieverID, senderID;

    public BasicMessageInfo(long messageNumber, int senderID, int recieverID){
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
}
