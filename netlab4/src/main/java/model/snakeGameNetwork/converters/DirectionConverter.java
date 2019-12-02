package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Direction;
import model.utils.Converter;
import model.utils.ConvertionExeption;

public class DirectionConverter implements Converter<Direction, SnakesProto.Direction> {
    @Override
    public SnakesProto.Direction convert(Direction direction) throws ConvertionExeption {
        switch(direction){
            case DOWN:
                return SnakesProto.Direction.DOWN;
            case UP:
                return SnakesProto.Direction.UP;
            case RIGHT:
                return SnakesProto.Direction.RIGHT;
            case LEFT:
                return SnakesProto.Direction.LEFT;
        }
        throw new ConvertionExeption("Wrong direction.");
    }

    @Override
    public Direction inverseConvert(SnakesProto.Direction protoDirection) throws ConvertionExeption{
        switch (protoDirection){
            case LEFT:
                return Direction.LEFT;
            case RIGHT:
                return Direction.RIGHT;
            case UP:
                return Direction.UP;
            case DOWN:
                return Direction.DOWN;
        }
        throw new ConvertionExeption("Wrong proto direction");
    }
}
