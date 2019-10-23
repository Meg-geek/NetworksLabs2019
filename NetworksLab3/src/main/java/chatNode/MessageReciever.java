package chatNode;

import node.NodeMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReciever implements Runnable {
    private DatagramSocket socket;
    private ChatNode chatNode;
    private boolean running = true;

    public MessageReciever(DatagramSocket socket, ChatNode chatNode){
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
                chatNode.addRecvMessage(new ChatNodeMessage(packet.getData()));
            } catch(IOException ex){
                running = false;
                ex.printStackTrace();
            }

        }
    }
}
