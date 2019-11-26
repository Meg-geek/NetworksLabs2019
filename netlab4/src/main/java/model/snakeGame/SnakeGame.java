package model.snakeGame;

import model.game.Game;
import model.game.GameField;
import model.game.GameSettings;
import model.game.Player;
import model.networkUtils.*;
import model.snakeGameNetwork.messages.GameStateMessage;
import model.snakeGameNetwork.messages.RoleChangeMessage;
import model.snakeGameNetwork.messages.SteerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SnakeGame implements Game, NetworkGame {
    private final int THREADS_AMOUNT = 1;
    private final int INIT_DELAY = 0;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private GameSettings gameSettings;
    private GameNetworkSettings gameNetworkSettings;
    private GameField gameField;
    private MasterNode master;
    private NetworkUser me;
    private NetworkApp app;
    private AtomicLong msgSeq = new AtomicLong(0);
    private List<NetworkUser> networkUserList;
    private boolean alive = true;

    //если мастер конструктор и если не мастер?

    SnakeGame(GameSettings gameSettings){
        this.gameSettings = gameSettings;
    }

    public SnakeGame(NetworkApp app, GameSettings gameSettings, GameNetworkSettings gameNetworkSettings,
              int masterID, List<NetworkUser> usersList, List<Player> playersList){
        this.app = app;
        this.gameSettings = gameSettings;
        this.gameNetworkSettings = gameNetworkSettings;
        //this.me = me;
    }

    @Override
    public void start() {
       scheduledThreadPool.scheduleWithFixedDelay(new MovementThread(gameField),
               INIT_DELAY,
               gameSettings.getStateDelayMS(),
               TimeUnit.MILLISECONDS);
    }

    private void changeRole(RoleChangeMessage message){
        NodeRole senderRole = message.getSenderRole(),
                recieverRole = message.getRecieverRole();
        if(senderRole == NodeRole.MASTER){
            master.replaceMaster();
        }
        //осознанно выходящий игрок
        if(senderRole == NodeRole.VIEWER){
            removeUser(message.getSenderID());
        }
        //от главного к умершему
        if(recieverRole == NodeRole.VIEWER){
            alive = false;
        }
        if(recieverRole == NodeRole.DEPUTY){
            master.replaceMaster();
        }
        //мы стали заместителем
        if(recieverRole == NodeRole.MASTER){
            becomeMaster();
        }
    }

    private void becomeMaster(){
        master.setDeputy(me);
        master.replaceMaster();
        for(NetworkUser user : networkUserList){
            Message message = new RoleChangeMessage(new BasicMessageInfo(msgSeq.getAndIncrement(),
                    me.getID(), user.getID()), NodeRole.MASTER, null);
            app.sendMessage(message, new ArrayList<>(){{add(user);}});
        }
        scheduledThreadPool.scheduleWithFixedDelay(new MovementThread(gameField),
                INIT_DELAY,
                gameSettings.getStateDelayMS(),
                TimeUnit.MILLISECONDS);
    }

    private void removeUser(int userID){
        NetworkUser userToRemove = null;
        for(NetworkUser user : networkUserList){
            if(user.getID() == userID){
                userToRemove = user;
            }
        }
        if(userToRemove != null){
            networkUserList.remove(userToRemove);
        }
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.getType()){
            case ROLE_CHANGE:
                if(message instanceof RoleChangeMessage){
                    changeRole((RoleChangeMessage)message);
                }
                break;
            case STEER:
                if(message instanceof SteerMessage){
                    gameField.setSnakeDirection(message.getSenderID(), ((SteerMessage) message).getDirection());
                }
                break;
            case JOIN:
                if(){

                }
                break;
            case STATE:
                if(message instanceof GameStateMessage){
                    gameField.changeState((GameStateMessage)message);
                   // checkDeputy(((GameStateMessage)message).getDeputy());
                }
                break;


        }
    }


    @Override
    public void quitGame() {
        if(me == master.getMaster()){
            Message message = new RoleChangeMessage(new BasicMessageInfo(msgSeq.getAndIncrement(), master.getMaster().getID(),
                    master.getDeputy().getID()), null, NodeRole.MASTER);
            app.sendMessage(message, new ArrayList<>(){{add(master.getDeputy());}});
            scheduledThreadPool.shutdown();
            scheduledThreadPool.shutdownNow();
        } else {
            Message message = new RoleChangeMessage(new BasicMessageInfo(msgSeq.getAndIncrement(),
                    me.getID(), master.getMaster().getID()), NodeRole.VIEWER, null);
            app.sendMessage(message, new ArrayList<>(){{add(master.getMaster());}});
        }
    }

    @Override
    public long getAndIncrementMsgSeq() {
        return msgSeq.getAndIncrement();
    }

    @Override
    public int getMyID() {
        return 0;
    }

    @Override
    public GameNetworkSettings getNetworkSettings() {
        return null;
    }

    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    /*@Override
    public List<NetworkUser> getUsersList() {
        return null;
    }*/

    @Override
    public boolean equals(NetworkGame game) {
        if(game.getNetworkSettings().getNodeTimeoutMs() != gameNetworkSettings.getNodeTimeoutMs()
            || game.getNetworkSettings().getPingDelayMs() != gameNetworkSettings.getPingDelayMs()){
            return false;
        }
        if(game.getMasterIP() != master.getIP()){
            return false;
        }
        GameSettings otherSettings = game.getGameSettings();
        if(otherSettings.getWidth() != gameSettings.getWidth()
            || otherSettings.getHeight() != gameSettings.getHeight()
            || otherSettings.getDeadFoodProb() != gameSettings.getDeadFoodProb()
            || otherSettings.getStateDelayMS() != gameSettings.getStateDelayMS()
            || otherSettings.getFoodStatic() != gameSettings.getFoodStatic()) {
            return false;
        }
        return true;
    }

    @Override
    public String getMasterIP() {
        return null;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof NetworkGame){
            return equals((NetworkGame)obj);
        }
        return super.equals(obj);
    }

}
