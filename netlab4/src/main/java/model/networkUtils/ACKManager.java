package model.networkUtils;

import java.util.List;

public interface ACKManager {
    void ackRecv(long messageNumber, NetworkUser user);
    void addMessage(Message message, List<NetworkUser> usersList);
    void changeTimeout(int newTimeout);
}
