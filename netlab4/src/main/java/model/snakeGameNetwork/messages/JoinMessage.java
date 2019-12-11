package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class JoinMessage extends Message {
    private String playerName;

    public JoinMessage(long msgSeq, int senderId, int receiverId, String playerName){
        super(new BasicMessageInfo(msgSeq, senderId, receiverId));
        this.playerName = playerName;
    }

    public JoinMessage(long msgSeq, String playerName){
        super(new BasicMessageInfo(msgSeq));
        this.playerName = playerName;
    }

    public String getPlayerName(){
        return playerName;
    }

    @Override
    public MessageType getType() {
        return MessageType.JOIN;
    }
}
