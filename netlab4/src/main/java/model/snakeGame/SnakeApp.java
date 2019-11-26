package model.snakeGame;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import model.game.App;
import model.networkUtils.*;
import model.snakeGameNetwork.*;
import model.snakeGameNetwork.messages.AnnouncmentMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnakeApp implements App, NetworkApp {
    //private List<NetworkGame> gamesList = new ArrayList<>();
    private Map<NetworkGame, Date> gamesMap = new ConcurrentHashMap<>();
    private MessageConverter messageConverter;
    private MulticastRecvThread multicastRecvThread;
    private Thread recvThread;
    private ACKManager ackManager;
    private boolean started = false;
    private Sender sender;
    private final int GAME_TIMEOUT_S = 1;
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
            if(message instanceof AnnouncmentMessage){
                updateGamesMap((AnnouncmentMessage) message, ip, port);
            }
        }

        if(message.getType() == MessageType.ERROR){
            //show error on interface
        } else {
            curGame.handleMessage(message);
        }
    }

    private synchronized void updateGamesMap(AnnouncmentMessage message, String ip, int port){
        SnakeGame snakeGame = new SnakeGame(this, message.getGameSettings(), message.getNetworkSettings(),
                message.getSenderID(), message.getUsersList(), message.getPlayersList());
        gamesMap.put(snakeGame, new Date());
        List<NetworkGame> gamesToRemove = new ArrayList<>();
        long nowTime = new Date().getTime();
        for(Map.Entry<NetworkGame, Date> entry : gamesMap.entrySet()){
            if(nowTime - entry.getValue().getTime() > GAME_TIMEOUT_S*1000){
                gamesToRemove.add(entry.getKey());
            }
        }
        for(NetworkGame game : gamesToRemove){
            gamesMap.remove(game);
        }
    }

}
