package model.snakeGame;

import model.game.*;
import model.networkUtils.*;
import model.snakeGameNetwork.SnakeGameACKManager;
import model.snakeGameNetwork.SnakeNetworkUser;
import model.snakeGameNetwork.messages.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MasterSnakeGame implements NetworkGame {
    private static final int THREADS_AMOUNT = 4;
    private static final int INIT_DELAY_MS = 0;
    private static final int FIRST_ID = 1;
    private final int ACK_DELAY = 100;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private Settings gameSettings;
    private GameField gameField;
    private MasterNode master;
    private NetworkApp app;
    private AtomicInteger nextId;
    private AtomicLong msgSeq = new AtomicLong(0);
    private Map<Integer, NetworkUser> idUserMap = new ConcurrentHashMap<>();
    private ACKManager ackManager;

    //when we create game as master
    MasterSnakeGame(NetworkApp app, Settings settings){
        this.gameSettings = settings;
        this.app = app;
        nextId = new AtomicInteger(FIRST_ID);
        master = new MasterPlayer(app.getMe(), nextId.incrementAndGet());
        gameField = new SnakeGameField(gameSettings, master.getID(), master);
        ackManager = new SnakeGameACKManager(this, settings.getNodeTimeoutMs());
        start();
    }

    //when we become master after normal
    MasterSnakeGame(NetworkApp app, Settings gameSettings,
                    GameState gameState, MasterNode masterNode, long msgSeq){
        this.app = app;
        this.gameSettings = gameSettings;
        this.master = masterNode;
        master.changeRole(NodeRole.MASTER);
        this.gameField = new SnakeGameField(gameSettings);
        this.msgSeq = new AtomicLong(msgSeq);
        nextId = new AtomicInteger(gameState.getMaxID() + 1);
        ackManager = new SnakeGameACKManager(this, gameSettings.getNodeTimeoutMs());
        for(SnakeGamePlayerI snakeGamePlayerI : gameState.getPlayersList()){
            if(snakeGamePlayerI.getID() != master.getID()){
                idUserMap.put(snakeGamePlayerI.getID(), snakeGamePlayerI);
            } else {
                master.getMaster().setIp(snakeGamePlayerI.getIP());
                snakeGamePlayerI.changeRole(NodeRole.MASTER);
            }
        }
        gameField = new SnakeGameField(gameSettings, gameState);
        start();
    }

    private void start() {
        //moves snakes
       scheduledThreadPool.scheduleWithFixedDelay(()->{
                    gameField.moveSnakes();
                    GameStateMessage gameStateMessage = getGameStateMessage();
                    sendMessage(gameStateMessage);
                    app.stateChanged(gameStateMessage);
               },
               INIT_DELAY_MS,
               gameSettings.getStateDelayMS(),
               TimeUnit.MILLISECONDS);
       scheduledThreadPool.scheduleWithFixedDelay(() -> app.sendMulticastMessage(getAnnouncment()), GameNetworkSettings.MULTICAST_INTERVAL_S, GameNetworkSettings.MULTICAST_INTERVAL_S, TimeUnit.SECONDS);
        scheduledThreadPool.scheduleWithFixedDelay(()->{
                    long nowTime = new Date().getTime();
                    for(NetworkUser user : idUserMap.values()){
                        if(nowTime - user.getLastActivity().getTime() > gameSettings.getPingDelayMs()){
                            sendMessage(new PingMessage(msgSeq.getAndIncrement(), master.getID(), user.getID()),
                                    user);
                        }
                    }
                },
                INIT_DELAY_MS,
                gameSettings.getPingDelayMs(),
                TimeUnit.MILLISECONDS);
        scheduledThreadPool.scheduleWithFixedDelay(()->{
                    long nowTime = new Date().getTime();
                    List<Integer> usersIdRemoveList = new ArrayList<>();
                    for(NetworkUser user : idUserMap.values()){
                        if(nowTime - user.getLastActivity().getTime() > gameSettings.getNodeTimeoutMs()){
                            usersIdRemoveList.add(user.getID());
                        }
                    }
                    for(Integer id : usersIdRemoveList){
                        removePlayer(id);
                    }
                },
                gameSettings.getNodeTimeoutMs(),
                gameSettings.getNodeTimeoutMs() + gameSettings.getPingDelayMs(),
                TimeUnit.MILLISECONDS);

        scheduledThreadPool.scheduleWithFixedDelay(ackManager, INIT_DELAY_MS, ACK_DELAY, TimeUnit.MILLISECONDS);
    }


    private GameStateMessage getGameStateMessage(){
        GameState gameState = gameField.getState();
        return new GameStateMessage(msgSeq.getAndIncrement(),
                master.getID(),
                gameState.getStateOrder(),
                gameState.getSnakesList(),
                gameState.getFoodList(),
                gameState.getPlayersList(),
                gameSettings);
    }

    private void removePlayer(int playerID){
        NetworkUser userRemove = idUserMap.remove(playerID);
        if(userRemove != null){
            gameField.removePlayer(playerID);
        }
    }

    private void addPlayer(JoinMessage message){
        SnakeGamePlayerI player = new SnakeGamePlayer(nextId.incrementAndGet(),
                message.getPlayerName(),
                SnakeGamePlayerI.BEGIN_SCORE,
                message.getIp(),
                message.getPort(),
                NodeRole.NORMAL);
        if(gameField.addPlayer(player)){
            idUserMap.put(player.getID(), player);
            if(master.getDeputy() == null){
                makeDeputy(player);
            }
            ACKMessage ackMessage = new ACKMessage(msgSeq.getAndIncrement(),
                    master.getID(), player.getID());
            sendMessage(ackMessage, player);
        } else {
            sendMessage(new ErrorMessage(msgSeq.getAndIncrement(), "The game doesn't have enough space"),
                    player);
        }
    }

    private void sendMessage(Message message, NetworkUser user){
        List<NetworkUser> usersList = new ArrayList<>(){{add(user);}};
        app.sendMessage(message, usersList);
        ackManager.addMessage(message, usersList);
    }

    private void sendMessage(Message message){
        List<NetworkUser> usersList = new ArrayList<>(idUserMap.values());
        if(usersList.size() > 0){
            app.sendMessage(message, usersList);
            ackManager.addMessage(message, usersList);
        }
    }

    private void makeDeputy(SnakeGamePlayerI playerI){
        master.setDeputy(playerI);
        playerI.changeRole(NodeRole.DEPUTY);
        sendMessage(new RoleChangeMessage(msgSeq.getAndIncrement(),
                master.getID(), playerI.getID(), NodeRole.MASTER, NodeRole.DEPUTY));
    }


    private void refreshActivity(int id){
        NetworkUser user = idUserMap.get(id);
        if(user != null){
            user.refreshActivity();
        }
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.getType()){
            case ACK:
                if (message instanceof ACKMessage){
                    ackManager.ackRecv(message.getNumber(),
                            new SnakeNetworkUser(message.getIp(), message.getPort()));
                }
                break;
            case ROLE_CHANGE:
                if(message instanceof RoleChangeMessage &&
                        ((RoleChangeMessage)message).getSenderRole() == NodeRole.VIEWER){
                    removePlayer(message.getSenderID());
                }
                break;
            case STEER:
                if(message instanceof SteerMessage){
                    //System.out.println("Steer msg senderId" + message.getSenderID() + " master id " + master.getID());
                    gameField.setSnakeDirection(message.getSenderID(), ((SteerMessage) message).getDirection());
                }
                break;
            case JOIN:
                if(message instanceof JoinMessage){
                    addPlayer((JoinMessage)message);
                }
                break;
            case PING:
                refreshActivity(message.getSenderID());
                break;
            default:
                System.out.println("wrong type of message got to master node "  + message.getType() + " " + message);
                break;
        }
        refreshActivity(message.getSenderID());
    }


    @Override
    public void quitGame() {
        if(master.getDeputy() != null){
            Message message = new RoleChangeMessage(msgSeq.getAndIncrement(), master.getMaster().getID(),
                    master.getDeputy().getID(), NodeRole.VIEWER, NodeRole.MASTER);
            app.sendMessage(message, new ArrayList<>(){{add(master.getDeputy());}});
        }
        scheduledThreadPool.shutdown();
        //scheduledThreadPool.shutdownNow();
    }

    @Override
    public int getPlayersAmount() {
        return idUserMap.size();
    }

    @Override
    public GameNetworkSettings getNetworkSettings() {
        return gameSettings;
    }

    @Override
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    @Override
    public boolean equals(NetworkGame game) {
        if(!game.getMaster().equals(this.master)){
            return false;
        }
        if(!game.getNetworkSettings().equals(this.gameSettings)){
            return false;
        }
        return game.getGameSettings().equals(this.gameSettings);
    }

    @Override
    public MasterNode getMaster() {
        return master;
    }

    @Override
    public void sendMessage(Message message, List<NetworkUser> usersList) {
        app.sendMessage(message, usersList);
    }


    private Message getAnnouncment() {
        GameState gameState = gameField.getState();
        return new AnnouncementMessage(msgSeq.getAndIncrement(), master.getID(), gameSettings,
                gameState.getPlayersList(), gameField.isJoinable());
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof NetworkGame){
            return equals((NetworkGame)obj);
        }
        return super.equals(obj);
    }

    @Override
    public void setSnakeDirection(Direction direction) {
        gameField.setSnakeDirection(master.getID(), direction);
    }
}
