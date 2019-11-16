package model.networkUtils;

public interface Message {
    long getNumber();
    int getSenderID();
    int getReceiverID();
    MessageType getType();
}
