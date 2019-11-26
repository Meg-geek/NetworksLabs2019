package model.snakeGame;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import model.game.App;
import model.game.Game;
import model.networkUtils.*;
import model.snakeGameNetwork.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnakeApp implements App, NetworkApp {
    private List<Game> gamesList = new ArrayList<>();
    private MessageConverter messageConverter;
    private MulticastRecvThread multicastRecvThread;
    private Thread recvThread;
    private ACKManager ackManager;
    private boolean started = false;
    private Sender sender;
    private final int ACK_DELAY_MS = 10;
    private final int THREADS_AMOUNT = 1;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private NetworkGame curGame;


    public SnakeApp() throws IOException{
        DatagramSocket socket = new DatagramSocket();
        messageConverter = new ProtoMessageConverter();
        sender = new Sender(socket);
        multicastRecvThread = new MulticastRecvThread(this, 9192, "239.192.0.4");
        recvThread = new Thread(new MessageReciever(socket, this));
        SnakeGameACKManager snakeGameACKManager = new SnakeGameACKManager(this);
        ackManager = snakeGameACKManager;
        scheduledThreadPool.scheduleWithFixedDelay(snakeGameACKManager, ACK_DELAY_MS, ACK_DELAY_MS
                , TimeUnit.MILLISECONDS);
    }

    @Override
    public void start() {
        if(!started){
            started = true;
            multicastRecvThread.start();
            recvThread.start();
        }
    }

    @Override
    public void recieveDatagramPacket(DatagramPacket packet) {
        try{
            Message message = messageConverter.protoToMessage(SnakesProto.GameMessage.parseFrom(packet.getData()));
            handleMessage(message, packet.getAddress().getHostAddress(), packet.getPort());
        } catch(InvalidProtocolBufferException ex){
            //log?
            ex.printStackTrace();
        }
    }

    /*@Override
    public void sendMessage(Message message) {

    }

     */

    @Override
    public void sendMessage(Message message, List<NetworkUser> users) {
        SnakesProto.GameMessage gameMessage = messageConverter.messageToProto(message);
        if(gameMessage != null){
            sender.send(gameMessage, users);
        }
    }

    private void handleMessage(Message message, String ip, int port){
        if(message.getType() == MessageType.ACK){
            ackManager.ackRecv(message.getNumber(), new SnakeNetworkUser(message.getSenderID(), null,
                    ip, port));
        }
        if(message.getType() == MessageType.ANNOUNCMENT){

        }

        if(message.getType() == MessageType.ERROR){
            //show error on interface
        } else {
            curGame.handleMessage(message);
        }
    }
}
