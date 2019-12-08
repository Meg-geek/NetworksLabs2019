package model.snakeGame;

import model.game.*;
import model.networkUtils.*;
import model.snakeGameNetwork.messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MasterSnakeGame implements Game, NetworkGame {
    private static final int THREADS_AMOUNT = 1;
    private static final int INIT_DELAY = 0;
    private static final int FIRST_ID = 0;
    private ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private GameSettings gameSettings;
    private GameNetworkSettings gameNetworkSettings;
    private GameField gameField;
    private MasterNode master;
    private NetworkApp app;
    private AtomicInteger nextId;

    //private NetworkUser me;
    private AtomicLong msgSeq = new AtomicLong(0);
    private List<NetworkUser> networkUserList = new CopyOnWriteArrayList<>();
    private boolean alive = true;

    //when we create game as master
    MasterSnakeGame(NetworkApp app, GameSettings gameSettings, GameNetworkSettings gameNetworkSettings){
        this.gameSettings = gameSettings;
        this.app = app;
        nextId = new AtomicInteger(FIRST_ID);
        this.gameNetworkSettings = gameNetworkSettings;
        master = new MasterPlayer(app.getMe(), nextId.getAndIncrement());
        gameField = new SnakeGameField(gameSettings, master.getID(), master);
        start();
    }

    //when we become master
    MasterSnakeGame(SnakeGame game){}

/*
    public MasterSnakeGame(NetworkApp app, GameSettings gameSettings, GameNetworkSettings gameNetworkSettings,
                           List<NetworkUser> usersList, List<Player> playersList, MasterNode master){
        this.app = app;
        this.gameSettings = gameSettings;
        this.gameNetworkSettings = gameNetworkSettings;
        this.master = master;
    }

 */

   // become a master
    private void start() {
       scheduledThreadPool.scheduleWithFixedDelay(()->{
                    gameField.moveSnakes();
                    app.sendMessage(getGameStateMessage(), networkUserList);
               },
               INIT_DELAY,
               gameSettings.getStateDelayMS(),
               TimeUnit.MILLISECONDS);
       scheduledThreadPool.scheduleWithFixedDelay(() -> app.sendMulticastMessage(getAnnouncment()),
                    INIT_DELAY, GameNetworkSettings.MULTICAST_INTERVAL_S, TimeUnit.SECONDS);
    }

    private Message getGameStateMessage(){
        GameState gameState = gameField.getState();
        return new GameStateMessage(msgSeq.getAndIncrement(),
                master.getID(),
                gameState.getStateOrder(),
                gameState.getSnakesList(),
                gameState.getFoodList(),
                gameState.getPlayersList(),
                gameSettings, gameNetworkSettings);
    }

    /*
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
        master = new MasterPlayer(me);
        for(NetworkUser user : networkUserList){
            Message message = new RoleChangeMessage(new BasicMessageInfo(msgSeq.getAndIncrement(),
                    me.getID(), user.getID()), NodeRole.MASTER, null);
            app.sendMessage(message, new ArrayList<>(){{add(user);}});
        }
        start();
    }


*/
    private void removePlayer(int playerID){
        NetworkUser userToRemove = null;
        for(NetworkUser user : networkUserList){
            if(user.getID() == playerID){
                userToRemove = user;
            }
        }
        if(userToRemove != null){
            networkUserList.remove(userToRemove);
        }
        gameField.removePlayer(playerID);
    }

    private void addPlayer(JoinMessage message){
        SnakeGamePlayerI player = new SnakeGamePlayer(nextId.getAndIncrement(),
                message.getPlayerName(),
                SnakeGamePlayerI.BEGIN_SCORE,
                message.getIp(),
                message.getPort(),
                NodeRole.NORMAL);
        if(gameField.addPlayer(player)){
            networkUserList.add(player);
            if(master.getDeputy() == null){
                makeDeputy(player);
            }
        } else {
            sendMessage(new ErrorMessage(msgSeq.getAndIncrement(), "The game doesn't have enough space"),
                    player);
        }
    }

    private void sendMessage(Message message, NetworkUser user){
        app.sendMessage(message, new ArrayList<>(){{add(user);}});
    }

    private void sendMessage(Message message){
        app.sendMessage(message, networkUserList);
    }

    private void makeDeputy(SnakeGamePlayerI playerI){
        master.setDeputy(playerI);
        playerI.changeRole(NodeRole.DEPUTY);
        sendMessage(new RoleChangeMessage(msgSeq.getAndIncrement(),
                master.getID(), playerI.getID(), NodeRole.MASTER, NodeRole.DEPUTY));
    }



    @Override
    public void handleMessage(Message message) {
        switch (message.getType()){
            case ROLE_CHANGE:
                if(message instanceof RoleChangeMessage &&
                        ((RoleChangeMessage)message).getSenderRole() == NodeRole.VIEWER){
                    removePlayer(message.getSenderID());
                }
                break;
            case STEER:
                if(message instanceof SteerMessage){
                    gameField.setSnakeDirection(message.getSenderID(), ((SteerMessage) message).getDirection());
                }
                break;
            case JOIN:
                if(message instanceof JoinMessage){
                    addPlayer((JoinMessage)message);
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
        if(master.getDeputy() != null){
            Message message = new RoleChangeMessage(msgSeq.getAndIncrement(), master.getMaster().getID(),
                    master.getDeputy().getID(), NodeRole.VIEWER, NodeRole.MASTER);
            app.sendMessage(message, new ArrayList<>(){{add(master.getDeputy());}});
        }
        scheduledThreadPool.shutdown();
        scheduledThreadPool.shutdownNow();
    }

    /*
    @Override
    public long getAndIncrementMsgSeq() {
        return msgSeq.getAndIncrement();
    }



    @Override
    public int getMyID() {
        return 0;
    }

     */

    @Override
    public GameNetworkSettings getNetworkSettings() {
        return gameNetworkSettings;
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
        if(!game.getNetworkSettings().equals(this.gameNetworkSettings)){
            return false;
        }
        return game.getGameSettings().equals(this.gameSettings);
    }

    /*
    @Override
    public MasterNode getMaster() {
        return master;
    }

     */

    @Override
    public Message getAnnouncment() {
        GameStateMessage gameState = (GameStateMessage) gameField.getState();
        return new AnnouncmentMessage(gameSettings, gameNetworkSettings,
                gameState.getSnakeGamePlayersList(), gameField.isJoinable());
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
