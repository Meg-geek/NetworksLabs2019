package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.SnakeState;
import model.utils.Converter;
import model.utils.ConvertionExeption;

public class SnakeStateConverter implements Converter<SnakeState, SnakesProto.GameState.Snake.SnakeState> {
    @Override
    public SnakesProto.GameState.Snake.SnakeState convert(SnakeState snakeState) throws ConvertionExeption {
        switch(snakeState){
            case ZOMBIE:
                return SnakesProto.GameState.Snake.SnakeState.ZOMBIE;
            case ALIVE:
                return SnakesProto.GameState.Snake.SnakeState.ALIVE;
        }
        throw new ConvertionExeption("Wrong type for convertion SnakeState. Snake game classes to proto");
    }

    @Override
    public SnakeState inverseConvert(SnakesProto.GameState.Snake.SnakeState snakeProtoState) throws ConvertionExeption {
        switch(snakeProtoState){
            case ALIVE:
                return SnakeState.ALIVE;
            case ZOMBIE:
                return SnakeState.ZOMBIE;
        }
        throw new ConvertionExeption("Wrong type for convertion SnakeState. Proto to snake game classes.");
    }
}
