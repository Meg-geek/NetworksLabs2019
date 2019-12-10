package model.networkUtils;

import java.util.List;

public interface ACKManager extends Runnable{
    void ackRecv(long messageNumber, NetworkUser user);
    void addMessage(Message message, List<NetworkUser> usersList);
}
