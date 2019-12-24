package model.networkUtils;

final public class BasicMessageInfo {
    private long messageNumber;
    private int recieverID, senderID;

    public BasicMessageInfo(long messageNumber, int senderID, int recieverID){
        this(messageNumber, senderID);
        this.recieverID = recieverID;
    }

    public BasicMessageInfo(long messageNumber, int senderID){
        this(messageNumber);
        this.senderID = senderID;
    }

    public BasicMessageInfo(long messageNumber){
        this.messageNumber = messageNumber;
    }

    public BasicMessageInfo(){}

    final void setRecieverID(int recieverID){
        this.recieverID = recieverID;
    }

    final public long getNumber(){
        return messageNumber;
    }

    final int getSenderID(){
        return senderID;
    }

    final int getReceiverID(){
        return recieverID;
    }
}
