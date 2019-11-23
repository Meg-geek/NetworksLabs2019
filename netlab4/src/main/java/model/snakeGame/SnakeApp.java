package model.snakeGame;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import model.game.App;
import model.game.Game;
import model.networkUtils.Message;
import model.networkUtils.MessageConverter;
import model.networkUtils.NetworkApp;
import model.networkUtils.NetworkUser;
import model.snakeGameNetwork.MulticastRecvThread;
import model.snakeGameNetwork.ProtoMessageConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class SnakeApp implements App, NetworkApp {
    private DatagramSocket multicastRecvSocket, socket;
    private List<Game> gamesList = new ArrayList<>();
    private NetworkUser myNetworkUser;
    private MessageConverter messageConverter;
    private MulticastRecvThread multicastRecvThread;
    private boolean started = false;

    public SnakeApp(){
        messageConverter = new ProtoMessageConverter();
    }

    @Override
    public void start() {
        if(!started){
            started = true;
            try{
                multicastRecvThread = new MulticastRecvThread(this, 9192, "239.192.0.4");
            } catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
    }

    //@Override
    //public void recieveMessage(Message message){
    //}

    @Override
    public void recieveDatagramPacket(DatagramPacket packet) {
        try{
            Message message = messageConverter.protoToMessage(SnakesProto.GameMessage.parseFrom(packet.getData()));
            handleMessage(message);
        } catch(InvalidProtocolBufferException ex){
            //log?
        }
    }

    private void handleMessage(Message message){
        switch (message.getType()){

        }
    }
}
