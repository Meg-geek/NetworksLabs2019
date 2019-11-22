package model.snakeGameNetwork;

import me.ippolitov.fit.snakes.SnakesProto;
import model.networkUtils.Message;
import model.networkUtils.MessageParser;
import model.networkUtils.NetworkUser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReciever implements Runnable {
    private boolean running = true;
    private DatagramSocket socket;
    private MessageParser messageParser;
    private NetworkUser networkUser;
    //temp
    private static int MAX_PACKET_LENGTH = 1500;

    public MessageReciever(DatagramSocket socket, MessageParser parser, NetworkUser user){
        this.socket = socket;
        messageParser = parser;
        networkUser = user;
    }

    @Override
    public void run() {
        while(running){
            try{
                DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH);
                socket.receive(packet);
                Message message = messageParser.parseMessage(SnakesProto.GameMessage.parseFrom(packet.getData()));
                networkUser.recieveMessage(message);
            } catch(IOException ex){
                throw new RuntimeException(ex);
            }

        }
    }
}
