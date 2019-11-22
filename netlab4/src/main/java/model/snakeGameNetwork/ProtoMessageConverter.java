package model.snakeGameNetwork;

import me.ippolitov.fit.snakes.SnakesProto;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageConverter;
import model.snakeGameNetwork.messages.*;

public class ProtoMessageConverter implements MessageConverter {

    @Override
    public Message protoToMessage(SnakesProto.GameMessage message) {
        BasicMessageInfo basicMessageInfo = new BasicMessageInfo(message.getMsgSeq(),
                message.getSenderId(), message.getReceiverId());
        switch(message.getTypeCase()){
            case ACK:
                return new ACKMessage(basicMessageInfo);
            case JOIN:
                return new JoinMessage(basicMessageInfo,
                        message.getJoin().getName());
            case PING:
                return new PingMessage(basicMessageInfo);
            case ERROR:
                return new ErrorMessage(basicMessageInfo,
                        message.getError().getErrorMessage());
            case STATE:
                return new GameStateMessage(basicMessageInfo,
                        message.getState().getState());
            case STEER:
                return new SteerMessage(basicMessageInfo,
                        message.getSteer().getDirection());
            case ROLE_CHANGE:
                return new RoleChangeMessage(basicMessageInfo, message.getRoleChange());
            case ANNOUNCEMENT:
                return new AnnouncmentMessage(basicMessageInfo, message.getAnnouncement());
            case TYPE_NOT_SET:
                //or log?
                return null;
        }
        return null;
    }

    @Override
    public SnakesProto.GameMessage messageToProto(Message message) {
        return null;
    }
}
