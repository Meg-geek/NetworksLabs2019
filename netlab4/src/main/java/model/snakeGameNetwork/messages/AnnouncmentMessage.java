package model.snakeGameNetwork.messages;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.GameSettings;
import model.game.Player;
import model.networkUtils.*;
import model.snakeGame.Settings;
import model.snakeGame.SnakeGamePlayer;
import model.snakeGameNetwork.SnakeNetworkUser;

import java.util.ArrayList;
import java.util.List;

public class AnnouncmentMessage extends Message {
    private boolean joinable;
    private GameSettings gameSettings;
    private GameNetworkSettings networkSettings;
    private List<Player> playersList = new ArrayList<>();
    private List<NetworkUser> usersList = new ArrayList<>();

    public AnnouncmentMessage(BasicMessageInfo messageInfo, SnakesProto.GameMessage.AnnouncementMsg announcementMsg){
        super(messageInfo);
        joinable = announcementMsg.getCanJoin();
        makeSettings(announcementMsg.getConfig());
        makePlayersList(announcementMsg.getPlayers().getPlayersList());
        makeUsersList(announcementMsg.getPlayers().getPlayersList());
    }

    private void makeUsersList(List<SnakesProto.GamePlayer> protoPlayersList){
        for(SnakesProto.GamePlayer player : protoPlayersList){
            usersList.add(new SnakeNetworkUser(player.getId(), getRole(player.getRole()),
                    player.getIpAddress(), player.getPort()));
        }
    }

    private NodeRole getRole(SnakesProto.NodeRole protoRole){
        switch(protoRole){
            case DEPUTY:
                return NodeRole.DEPUTY;
            case MASTER:
                return NodeRole.MASTER;
            case NORMAL:
                return NodeRole.NORMAL;
            case VIEWER:
                return NodeRole.VIEWER;
        }
        return null;
    }

    private void makePlayersList(List<SnakesProto.GamePlayer> protoPlayersList){
        for(SnakesProto.GamePlayer player : protoPlayersList){
            playersList.add(new SnakeGamePlayer(player.getId(), player.getName(), player.getScore()));
        }
    }

    private void makeSettings(SnakesProto.GameConfig gameConfig){
        Settings settings = new Settings();
        settings.setHeight(gameConfig.getHeight());
        settings.setWidth(gameConfig.getWidth());
        settings.setDeadFoodProb(gameConfig.getDeadFoodProb());
        settings.setFoodStatic(gameConfig.getFoodStatic());
        settings.setStateDelayMs(gameConfig.getStateDelayMs());

        settings.setNodeTimeoutMs(gameConfig.getNodeTimeoutMs());
        settings.setPingDelayMs(gameConfig.getPingDelayMs());
        gameSettings = settings;
        networkSettings = settings;
    }

    @Override
    public MessageType getType() {
        return MessageType.ANNOUNCMENT;
    }

    public boolean isJoinable(){
        return joinable;
    }

    public GameNetworkSettings getNetworkSettings(){
        return networkSettings;
    }

    public GameSettings getGameSettings(){
        return gameSettings;
    }

    public List<Player> getPlayersList(){
        return playersList;
    }

    public List<NetworkUser> getUsersList(){
        return usersList;
    }
}
