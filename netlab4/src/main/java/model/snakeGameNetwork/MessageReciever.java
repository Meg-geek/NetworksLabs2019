package model.snakeGameNetwork;

import model.networkUtils.NetworkApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReciever implements Runnable {
    private boolean running = true;
    private DatagramSocket socket;
    private NetworkApp networkApp;
    //temp
    private static int MAX_PACKET_LENGTH = 1500;

    public MessageReciever(DatagramSocket socket, NetworkApp networkApp){
        this.socket = socket;
        this.networkApp = networkApp;
    }

    @Override
    public void run() {
        while(running){
            try{
                DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH);
                socket.receive(packet);
                networkApp.recieveDatagramPacket(packet);
            } catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
    }
}
