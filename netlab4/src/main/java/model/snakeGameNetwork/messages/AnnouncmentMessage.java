package model.snakeGameNetwork.messages;

import model.game.GameSettings;
import model.game.SnakeGamePlayerI;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.GameNetworkSettings;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

import java.util.List;

public class AnnouncmentMessage extends Message {
    private boolean joinable;
    private GameSettings gameSettings;
    private GameNetworkSettings networkSettings;
    private List<SnakeGamePlayerI> playersList;

    public AnnouncmentMessage(long msgSeq, GameSettings settings, GameNetworkSettings networkSettings,
                              List<SnakeGamePlayerI> playersList, boolean canJoin){
        super(new BasicMessageInfo(msgSeq));
        this.joinable = canJoin;
        this.gameSettings = settings;
        this.networkSettings = networkSettings;
        this.playersList = playersList;
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

    public List<SnakeGamePlayerI> getPlayersList(){
        return playersList;
    }
}
