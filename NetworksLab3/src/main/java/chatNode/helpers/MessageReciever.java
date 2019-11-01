package chatNode.helpers;

import chatNode.ChatNodeMessage;
import node.MessagesNode;
import node.NodeMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReciever implements Runnable {
    private DatagramSocket socket;
    private MessagesNode chatNode;
    private boolean running = true;

    public MessageReciever(DatagramSocket socket, MessagesNode chatNode){
        this.socket = socket;
        this.chatNode = chatNode;
    }

    @Override
    public void run() {
        while(running){
            try{
                DatagramPacket packet = new DatagramPacket(
                        new byte[NodeMessage.MAX_MESSAGE_LENGTH], NodeMessage.MAX_MESSAGE_LENGTH);
                socket.receive(packet);
                if (Math.random()*100 < chatNode.getLossPerc()){
                    chatNode.addRecvMessage(new ChatNodeMessage(packet.getData(),
                            packet.getAddress().getHostAddress(), packet.getPort()));
                }
            } catch(IOException ex){
                running = false;
                ex.printStackTrace();
            }

        }
    }
}
