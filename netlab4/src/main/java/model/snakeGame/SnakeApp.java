package model.snakeGame;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import model.game.App;
import model.game.GameSettings;
import model.networkUtils.*;
import model.snakeGameNetwork.*;
import model.snakeGameNetwork.messages.ACKMessage;
import model.snakeGameNetwork.messages.AnnouncmentMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnakeApp implements App, NetworkApp {
    //список текущих игр, обновляется при получении AnnouncmentMessage
    private Map<NetworkGame, Date> gamesMap = new ConcurrentHashMap<>();
    //преобразует сообщения типа Message в тип для протокола
    private MessageConverter messageConverter;
    //поток, принимающий мультикаст сообщения типа AnnouncmentMessage
    private MulticastRecvThread multicastRecvThread;
    //поток, обрабатывающий сообщения, приходящие на сокет
    private Thread recvThread;
    //утилита, отправка сообщений на какой-либо хост
    private Sender sender;
    //время, через которое рассылаются AnnouncmentMessage мультикастом
    private final int GAME_TIMEOUT_S = 1;
    //коэфициент ожидания между получением нового AnnouncmentMessage
    private final int WAITING_COEF = 3;
    //чило потоков в ScheduledThreadPoolExecutor
    private final int THREADS_AMOUNT = 1;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    //текущая активная игра
    private NetworkGame curGame;
    private NetworkUser myUser;
    private DatagramSocket socket;
    private MulticastSocket multicastSocket;


    public SnakeApp() throws IOException{
        socket = new DatagramSocket();
        multicastSocket = new MulticastSocket(GameNetworkSettings.MULTICAST_PORT);
        multicastSocket.joinGroup(InetAddress.getByName(GameNetworkSettings.MULTICAST_ADDRESS));
        messageConverter = new ProtoMessageConverter();
        sender = new Sender(socket);
        multicastRecvThread = new MulticastRecvThread(this, multicastSocket);
        recvThread = new Thread(new MessageReciever(socket, this));
       scheduledThreadPool.scheduleWithFixedDelay(this::refreshGames, GAME_TIMEOUT_S*WAITING_COEF,
               GAME_TIMEOUT_S*WAITING_COEF, TimeUnit.SECONDS);
        multicastRecvThread.start();
        recvThread.start();
    }

    @Override
    public void createGame(GameSettings gameSettings, GameNetworkSettings networkSettings){
        curGame = new MasterSnakeGame(this, gameSettings, networkSettings);
    }

    @Override
    public void setPlayerName(String name){
        myUser = new SnakeGamePlayer(name, socket.getPort());
    }


    @Override
    public void recieveDatagramPacket(DatagramPacket packet) {
        try{
            Message message = messageConverter.protoToMessage(SnakesProto.GameMessage.parseFrom(packet.getData()));
            message.setIp(packet.getAddress().getHostAddress());
            message.setPort(packet.getPort());
            handleMessage(message);
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

    @Override
    public NetworkUser getMe() {
        return myUser;
    }

    @Override
    public void sendMulticastMessage(Message message) {
        byte[] msgBytes = messageConverter.messageToProto(message).toByteArray();
        try{
            multicastSocket.send(new DatagramPacket(msgBytes, msgBytes.length));
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void startAsMaster(NetworkGame prevGame, NetworkGame curGame) {
        if(this.curGame == prevGame && prevGame instanceof SnakeGame && curGame instanceof MasterSnakeGame){
            this.curGame = curGame;
        }
    }

    private void handleMessage(Message message){
        switch(message.getType()){
            case ANNOUNCMENT:
                if(message instanceof AnnouncmentMessage){
                    updateGamesMap((AnnouncmentMessage) message);
                }
                break;
            case ERROR:
                //show
                curGame.handleMessage(message);
                break;
            default:
                curGame.handleMessage(message);
        }
        if(!(message instanceof ACKMessage)){
            sendMessage(new ACKMessage(message.getNumber()),
                    new ArrayList<>(){{add(new SnakeNetworkUser(message.getIp(), message.getPort()));}});
        }
    }

    private void updateGamesMap(AnnouncmentMessage message){
        SnakeGame snakeGame = new SnakeGame(this,
                message.getGameSettings(),
                message.getNetworkSettings(),
                message.getPlayersList(),
                new MasterPlayer(new SnakeNetworkUser(message.getSenderID(), NodeRole.MASTER,
                        message.getIp(), message.getPort()))
                );
        gamesMap.put(snakeGame, new Date());
    }


    private synchronized void refreshGames(){
        List<NetworkGame> gamesToRemove = new ArrayList<>();
        long nowTime = new Date().getTime();
        for(Map.Entry<NetworkGame, Date> entry : gamesMap.entrySet()){
            if(nowTime - entry.getValue().getTime() > GAME_TIMEOUT_S*1000*WAITING_COEF){
                gamesToRemove.add(entry.getKey());
            }
        }
        for(NetworkGame game : gamesToRemove){
            gamesMap.remove(game);
        }
    }
}
