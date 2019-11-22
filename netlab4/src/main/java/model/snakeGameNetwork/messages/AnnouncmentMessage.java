package model.snakeGameNetwork.messages;

import me.ippolitov.fit.snakes.SnakesProto;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;

public class AnnouncmentMessage extends Message {
    private boolean joinable;

    public AnnouncmentMessage(BasicMessageInfo messageInfo, SnakesProto.GameMessage.AnnouncementMsg announcementMsg){
        super(messageInfo);
        joinable = announcementMsg.getCanJoin();
    }

    @Override
    public MessageType getType() {
        return MessageType.ANNOUNCMENT;
    }

    public boolean isJoinable(){
        return joinable;
    }
}
