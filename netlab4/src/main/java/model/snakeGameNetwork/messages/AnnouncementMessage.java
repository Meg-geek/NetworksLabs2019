package model.snakeGameNetwork.messages;

import model.game.SnakeGamePlayerI;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;
import model.snakeGame.Settings;

import java.util.List;

public class AnnouncementMessage extends Message {
    private boolean joinable;
    private Settings gameSettings;
    private List<SnakeGamePlayerI> playersList;

    public AnnouncementMessage(long msgSeq, int senderId, Settings settings,
                               List<SnakeGamePlayerI> playersList, boolean canJoin){
        super(new BasicMessageInfo(msgSeq, senderId));
        this.joinable = canJoin;
        this.gameSettings = settings;
        this.playersList = playersList;
    }

    @Override
    public MessageType getType() {
        return MessageType.ANNOUNCEMENT;
    }

    public boolean isJoinable(){
        return joinable;
    }

    public Settings getGameSettings(){
        return gameSettings;
    }

    public List<SnakeGamePlayerI> getPlayersList(){
        return playersList;
    }
}
