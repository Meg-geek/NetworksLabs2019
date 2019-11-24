package model.snakeGame;

import model.game.Game;
import model.game.GameField;
import model.game.GameSettings;
import model.networkUtils.*;
import model.snakeGameNetwork.messages.RoleChangeMessage;

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

    SnakeGame(NetworkApp app, NetworkUser me){
        this.app = app;
        this.me = me;
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
}
