package model.networkUtils;

import me.ippolitov.fit.snakes.SnakesProto;

public interface MessageParser {
    Message parseMessage(SnakesProto.GameMessage message);
}
