package model.networkUtils;

import java.util.Date;

public interface NetworkUser {
    String getIP();
    int getPort();
    int getID();
    NodeRole getRole();
    void changeRole(NodeRole nodeRole);
   // void sendMessage(Message message, List<NetworkUser> usersList);
    Date getLastActivity();
    void refreshActivity();
    boolean equals(Object o);
  //  void recieveMessage(Message message);
}
