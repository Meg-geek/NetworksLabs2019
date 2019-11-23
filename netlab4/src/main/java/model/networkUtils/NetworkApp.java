package model.networkUtils;

import java.net.DatagramPacket;

public interface NetworkApp {
    //void recieveMessage(Message message);
    void recieveDatagramPacket(DatagramPacket packet);
}
