package model.networkUtils;

import java.net.DatagramPacket;
import java.util.List;

public interface NetworkApp {
    //void recieveMessage(Message message);
    void recieveDatagramPacket(DatagramPacket packet);
    //void sendMessage(Message message);
    void sendMessage(Message message, List<NetworkUser> users);
    NetworkUser getMe();
    //long getAndIncrementMsgSeq();
   // int getMyID();
   // void joinGame();
    void sendMulticastMessage(Message message);
    void startAsMaster(NetworkGame prevGame, NetworkGame curGame);
}
