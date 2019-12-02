package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Coordinates;
import model.snakeGame.Point;
import model.utils.Converter;

public class CoordinatesConverter implements Converter<Coordinates, SnakesProto.GameState.Coord> {
    @Override
    public SnakesProto.GameState.Coord convert(Coordinates coord) {
        return SnakesProto.GameState.Coord.newBuilder()
                .setX(coord.getX())
                .setY(coord.getY())
                .build();
    }

    @Override
    public Coordinates inverseConvert(SnakesProto.GameState.Coord protoCoord){
        return new Point(protoCoord.getX(), protoCoord.getY());
    }
}
