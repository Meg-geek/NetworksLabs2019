package model.snakeGame;

import SnakeGameInterfaces.Controller;
import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;
import model.game.App;
import model.game.Direction;
import model.game.GameSettings;
import model.networkUtils.*;
import model.snakeGameNetwork.*;
import model.snakeGameNetwork.messages.*;
import model.utils.ConvertionExeption;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnakeApp implements App, NetworkApp {
    //список текущих игр, обновляется при получении AnnouncmentMessage
    private Map<NetworkGame, Date> gamesMap = new ConcurrentHashMap<>();
    private Map<String, NetworkGame> ipGamesMap = new ConcurrentHashMap<>();
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
    private final int WAITING_COEF = 4;
    private final int DELAY_COEF = 2;
    //чило потоков в ScheduledThreadPoolExecutor
    private final int THREADS_AMOUNT = 1;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    //текущая активная игра
    private NetworkGame curGame;
    private NetworkUser myUser;
    private DatagramSocket socket;
    private MulticastSocket multicastSocket;
    private Controller gameController;
    private InetAddress group;

    public SnakeApp(String userName, Controller gameController) throws IOException{
        socket = new DatagramSocket();
        multicastSocket = new MulticastSocket(GameNetworkSettings.MULTICAST_PORT);
        multicastSocket.setReuseAddress(true);
        group = InetAddress.getByName(GameNetworkSettings.MULTICAST_ADDRESS);
        multicastSocket.joinGroup(group);
        messageConverter = new ProtoMessageConverter();
        sender = new Sender(socket);
        multicastRecvThread = new MulticastRecvThread(this, multicastSocket);
        recvThread = new MessageReciever(socket, this);
        scheduledThreadPool.scheduleWithFixedDelay(this::refreshGames, GAME_TIMEOUT_S*DELAY_COEF,
               GAME_TIMEOUT_S*DELAY_COEF, TimeUnit.SECONDS);
        multicastRecvThread.start();
        recvThread.start();
        myUser = new SnakeNetworkUser(socket.getPort(), userName);
        this.gameController = gameController;
    }

    @Override
    public void createGame(GameSettings gameSettings, GameNetworkSettings networkSettings){
        curGame = new MasterSnakeGame(this, new Settings(gameSettings, networkSettings));
    }

    @Override
    public void quit() {
        scheduledThreadPool.shutdown();
        scheduledThreadPool.shutdownNow();
        multicastRecvThread.interrupt();
        recvThread.interrupt();
        if(curGame != null){
            curGame.quitGame();
        }
    }


    @Override
    public void recieveDatagramPacket(DatagramPacket packet) {
        try {
            byte[] recvData = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
            SnakesProto.GameMessage gameMessage = SnakesProto.GameMessage.parseFrom(recvData);
            Message message = messageConverter.protoToMessage(gameMessage);
            if(message != null){
                message.setIp(packet.getAddress().getHostAddress());
                message.setPort(packet.getPort());
                handleMessage(message);
            }
        } catch (InvalidProtocolBufferException | ConvertionExeption ex) {
            //log?
            ex.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Message message, List<NetworkUser> users) {
        try{
            SnakesProto.GameMessage gameMessage = messageConverter.messageToProto(message);
            if(gameMessage != null){
                sender.send(gameMessage, users);
            }
        } catch(ConvertionExeption ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public NetworkUser getMe() {
        return myUser;
    }

    @Override
    public void sendMulticastMessage(Message message) {
        try{
            byte[] msgBytes = messageConverter.messageToProto(message).toByteArray();
            multicastSocket.send(new DatagramPacket(msgBytes, msgBytes.length,
                    group, GameNetworkSettings.MULTICAST_PORT));
        } catch(IOException ex){
            throw new RuntimeException(ex);
        } catch (ConvertionExeption ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void startAsMaster(NetworkGame prevGame, NetworkGame curGame) {
        if(this.curGame == prevGame && prevGame instanceof NormalSnakeGame && curGame instanceof MasterSnakeGame){
            this.curGame = curGame;
        }
    }

    private void handleMessage(Message message){
        if(message == null){
            return;
        }
        switch(message.getType()){
            case ANNOUNCEMENT:
                if(message instanceof AnnouncementMessage){
                    updateGamesMap((AnnouncementMessage) message);
                }
                break;
            case ERROR:
                if(message instanceof ErrorMessage){
                    ErrorMessage errorMessage = (ErrorMessage)message;
                    gameController.showError(errorMessage.getErrorMessage());
                }
                if(curGame != null){
                    curGame.handleMessage(message);
                }
                break;
            case STATE:
                if(message instanceof GameStateMessage){
                    GameStateMessage gameStateMessage = (GameStateMessage)message;
                    if(curGame != null){
                        curGame.handleMessage(message);
                        stateChanged(gameStateMessage);
                    }
                }
            default:
                if(curGame != null){
                    curGame.handleMessage(message);
                }
        }
        if(!(message instanceof ACKMessage || message instanceof AnnouncementMessage
                || curGame == null || message instanceof JoinMessage)){
            sendMessage(new ACKMessage(message.getNumber()),
                    new ArrayList<>(){{add(new SnakeNetworkUser(message.getIp(), message.getPort()));}});
        }
    }

    private void updateGamesMap(AnnouncementMessage message){
        NormalSnakeGame snakeGame = new NormalSnakeGame(this,
                message.getGameSettings(),
                message.getPlayersList(),
                new MasterPlayer(new SnakeNetworkUser(message.getSenderID(), NodeRole.MASTER,
                        message.getIp(), message.getPort()))
                );
        if(!ipGamesMap.containsKey(message.getIp())){
            ipGamesMap.put(message.getIp(), snakeGame);
            gamesMap.put(snakeGame, new Date());
        } else {
            NetworkGame game = ipGamesMap.get(message.getIp());
            gamesMap.replace(game, new Date());
        }
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
            ipGamesMap.remove(game.getMaster().getIP());
            gamesMap.remove(game);
        }
        gameController.updateGamesList(new ArrayList<>(gamesMap.keySet()));
    }

    @Override
    public void setDirection(Direction direction){
        if(curGame != null){
            curGame.setSnakeDirection(direction);
        }
    }

    @Override
    public void quitGame() {
        if(curGame != null){
            curGame.quitGame();
            curGame = null;
        }
    }

    @Override
    public void joinGame(String ip) {
        NetworkGame game = ipGamesMap.get(ip);
        if(game != null && curGame == null){
            ((NormalSnakeGame)game).startGame();
            curGame = game;
        }
    }

    @Override
    public void stateChanged(GameStateMessage gameStateMessage){
        gameController.updateGameState(new SnakeGameState(gameStateMessage.getStateOrder(),
                gameStateMessage.getSnakesList(),
                gameStateMessage.getFoodCoordsList(),
                gameStateMessage.getSnakeGamePlayersList()));
    }
}
