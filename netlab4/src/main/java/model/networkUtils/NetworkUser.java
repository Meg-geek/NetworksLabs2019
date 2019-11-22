package model.networkUtils;

import java.util.Date;
import java.util.List;

public interface NetworkUser {
    String getIP();
    int getPort();
    NodeRole getRole();
    void changeRole(NodeRole nodeRole);
    void sendMessage(Message message, List<NetworkUser> usersList);
    Date getLastActivity();
    void refreshActivity();
    void recieveMessage(Message message);
}
