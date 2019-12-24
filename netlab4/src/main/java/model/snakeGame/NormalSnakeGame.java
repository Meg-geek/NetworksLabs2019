package model.snakeGame;

import model.game.Direction;
import model.game.GameField;
import model.game.GameSettings;
import model.game.SnakeGamePlayerI;
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
import java.util.concurrent.atomic.AtomicLong;

public class NormalSnakeGame implements NetworkGame {
    private NetworkApp app;
    private Settings gameSettings;
    private List<SnakeGamePlayerI> playersList;
    private MasterNode masterNode;
    private final int FIRST_MSG_SEQ = 0;
    private AtomicLong msgSeq = new AtomicLong(FIRST_MSG_SEQ);
    private SnakeGamePlayerI myPlayer;
    private boolean isWaitingForId = true;
    private ACKManager ackManager;
    private final int THREADS_AMOUNT = 2;
    private ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private final int INIT_DELAY_MS = 10;
    private final int ACK_DELAY_MS = GameNetworkSettings.nodeTimeoutMSConst.getMinValue();
    private Map<Integer, SnakeGamePlayerI> idPlayerMap = new ConcurrentHashMap<>();
    private GameField gameField;
    private boolean isBecomingMaster = false;

    //from Announcment message
    NormalSnakeGame(NetworkApp app,
                    Settings settings,
                    List<SnakeGamePlayerI> playersList,
                    MasterNode masterNode
                     ){
        this.app = app;
        this.gameSettings = settings;
        this.playersList = playersList;
        this.masterNode = masterNode;
        gameField = new SnakeGameField(gameSettings);
        myPlayer = new SnakeGamePlayer(app.getMe().getName());
    }

    @Override
    public void setSnakeDirection(Direction direction) {
        sendMessage(new SteerMessage(msgSeq.getAndIncrement(), myPlayer.getID(), masterNode.getID(),
                direction));
    }

    private void sendMessage(Message message){
        List <NetworkUser> usersList = new ArrayList<>(){{add(masterNode);}};
        app.sendMessage(message, usersList);
        ackManager.addMessage(message, usersList);
    }

    @Override
    public void quitGame() {
        if(!isBecomingMaster){
            sendMessage(new RoleChangeMessage(msgSeq.getAndIncrement(), myPlayer.getID(), masterNode.getID(),
                    NodeRole.VIEWER, null));
        }
        threadPoolExecutor.shutdown();
       // threadPoolExecutor.shutdownNow();
    }

    @Override
    public int getPlayersAmount() {
        return playersList.size();
    }

    void startGame(){
        ackManager = new SnakeGameACKManager(this, gameSettings.getNodeTimeoutMs());
        sendMessage(new JoinMessage(msgSeq.getAndIncrement(), myPlayer.getName()));

        threadPoolExecutor.scheduleWithFixedDelay(ackManager, INIT_DELAY_MS, ACK_DELAY_MS, TimeUnit.MILLISECONDS);
        //ping sender
        threadPoolExecutor.scheduleWithFixedDelay(()-> sendMessage(new PingMessage(msgSeq.getAndIncrement(), myPlayer.getID(), masterNode.getID())), INIT_DELAY_MS, gameSettings.getPingDelayMs(), TimeUnit.MILLISECONDS);
        //controls master
        threadPoolExecutor.scheduleWithFixedDelay(()->{
            if(new Date().getTime() - masterNode.getLastActivity().getTime() > gameSettings.getNodeTimeoutMs()){
                if(masterNode.getDeputy().equals(myPlayer)){
                    becomeMaster();
                }
                masterNode.replaceMaster();
            }
        }, INIT_DELAY_MS, gameSettings.getNodeTimeoutMs(), TimeUnit.MILLISECONDS);
    }

    private void becomeMaster(){
        masterNode.setDeputy(myPlayer);
        masterNode.replaceMaster();
        MasterSnakeGame masterSnakeGame = new MasterSnakeGame(app, gameSettings,
                gameField.getState(), masterNode, msgSeq.get());
        app.startAsMaster(this, masterSnakeGame);
        quitGame();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.getType()){
            case ACK:
                if(message instanceof ACKMessage){
                    handleACK((ACKMessage)message);
                }
                break;
            case ERROR:
                if(message instanceof ErrorMessage){
                    quitGame();
                }
                break;
            case PING:
                if(message instanceof PingMessage && message.getSenderID() == masterNode.getID()){
                    masterNode.refreshActivity();
                }
                break;
            case ROLE_CHANGE:
                if(message instanceof RoleChangeMessage){
                    handleRoleChangeMsg((RoleChangeMessage)message);
                }
                break;
            case STATE:
                if(message instanceof GameStateMessage){
                    gameField.changeState((GameStateMessage)message);
                }
                break;
        }
        if(message.getSenderID() == masterNode.getID()){
            masterNode.refreshActivity();
        }
    }

    private void handleRoleChangeMsg(RoleChangeMessage roleChangeMessage){
        if(roleChangeMessage.getSenderRole() == NodeRole.MASTER){
            masterNode.setDeputy(new MasterPlayer(new SnakeNetworkUser(roleChangeMessage.getSenderID(),
                    roleChangeMessage.getSenderRole(), roleChangeMessage.getIp(), roleChangeMessage.getPort())));
            masterNode.replaceMaster();
            masterNode.refreshActivity();
        }
        if(roleChangeMessage.getRecieverRole() == NodeRole.VIEWER){
            myPlayer.changeRole(NodeRole.VIEWER);
        }
        if(roleChangeMessage.getRecieverRole() == NodeRole.DEPUTY){
            if(roleChangeMessage.getReceiverID() == myPlayer.getID()){
                //нас назначили заместителем
                masterNode.setDeputy(myPlayer);
            } else {
                SnakeGamePlayerI playerI = idPlayerMap.get(roleChangeMessage.getReceiverID());
                if(playerI != null){
                    masterNode.setDeputy(playerI);
                }
            }
        }
        if(roleChangeMessage.getRecieverRole() == NodeRole.MASTER){
            becomeMaster();
        }
    }

    private void handleACK(ACKMessage ackMessage){
        if(isWaitingForId){
            myPlayer.setID(ackMessage.getReceiverID());
            isWaitingForId = false;
        } else {
            ackManager.ackRecv(ackMessage.getNumber(), new SnakeNetworkUser(ackMessage.getSenderID(),
                    null, ackMessage.getIp(), ackMessage.getPort()));
        }
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
        if(!game.getMaster().equals(this.masterNode)){
            return false;
        }
        if(!game.getNetworkSettings().equals(this.gameSettings)){
            return false;
        }
        return game.getGameSettings().equals(this.gameSettings);
    }

    @Override
    public MasterNode getMaster() {
        return masterNode;
    }

    @Override
    public void sendMessage(Message message, List<NetworkUser> usersList) {
        app.sendMessage(message, usersList);
    }
}
