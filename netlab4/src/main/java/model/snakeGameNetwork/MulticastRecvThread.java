package model.snakeGameNetwork;

import model.networkUtils.NetworkApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastRecvThread extends Thread {
    private MulticastSocket multicastSocket;
    private boolean running = true;
    private NetworkApp app;

    public MulticastRecvThread(NetworkApp app, MulticastSocket socket){
        this.multicastSocket = socket;
        this.app = app;
    }

    @Override
    public void run() {
        while(!isInterrupted()){
            try{
                DatagramPacket packet = new DatagramPacket(new byte[ProtoMessageConverter.MAX_MSG_SIZE],
                        ProtoMessageConverter.MAX_MSG_SIZE);
                multicastSocket.receive(packet);
                app.recieveDatagramPacket(packet);
            } catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
        multicastSocket.close();
    }
}
