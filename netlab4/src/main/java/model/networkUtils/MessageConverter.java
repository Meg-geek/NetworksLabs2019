package model.networkUtils;

import me.ippolitov.fit.snakes.SnakesProto;

public interface MessageConverter {
    Message protoToMessage(SnakesProto.GameMessage message);
    SnakesProto.GameMessage messageToProto(Message message);
}
