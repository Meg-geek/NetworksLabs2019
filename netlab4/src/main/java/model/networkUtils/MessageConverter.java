package model.networkUtils;

import me.ippolitov.fit.snakes.SnakesProto;
import model.utils.ConvertionExeption;

public interface MessageConverter {
    Message protoToMessage(SnakesProto.GameMessage message) throws ConvertionExeption;
    SnakesProto.GameMessage messageToProto(Message message) throws ConvertionExeption;
}
