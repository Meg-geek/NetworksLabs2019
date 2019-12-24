package model.networkUtils;

import java.util.Date;

public interface NetworkUser {
    String getIP();
    void setIp(String ip);
    int getPort();
    int getID();
    void setID(int newID);
    NodeRole getRole();
    void changeRole(NodeRole nodeRole);
   // void sendMessage(Message message, List<NetworkUser> usersList);
    Date getLastActivity();
    void refreshActivity();
    boolean equals(Object o);
    String getName();
    void setName(String name);
  //  void recieveMessage(Message message);
}
