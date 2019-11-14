package model.networkUtils;

import java.util.List;

public interface ACKManager {
    void askRecv(long messageNumber, NetworkUser user);
    void addMessage(Message message, List<NetworkUser> usersList);
}
