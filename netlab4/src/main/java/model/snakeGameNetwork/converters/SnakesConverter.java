package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Coordinates;
import model.game.Direction;
import model.game.SnakeI;
import model.game.SnakeState;
import model.snakeGame.Snake;
import model.utils.Converter;
import model.utils.ConvertionExeption;

import java.util.ArrayList;
import java.util.List;

public class SnakesConverter implements Converter<List<SnakeI>, List<SnakesProto.GameState.Snake>> {
    private Converter<Direction, SnakesProto.Direction> directionConverter =
            new DirectionConverter();
    private Converter<List<Coordinates>, List<SnakesProto.GameState.Coord>> snakeBodyConverter
            = new SnakeBodyConverter();
    private Converter<SnakeState, SnakesProto.GameState.Snake.SnakeState> snakeStateConverter =
            new SnakeStateConverter();

    @Override
    public List<SnakesProto.GameState.Snake> convert(List<SnakeI> snakesIList) throws ConvertionExeption {
        List<SnakesProto.GameState.Snake> snakesProtoList = new ArrayList<>();
        for(SnakeI snakeI : snakesIList){
            SnakesProto.GameState.Snake.Builder snakeBuilder = SnakesProto.GameState.Snake.newBuilder();
            snakesProtoList.add(snakeBuilder
                    .setHeadDirection(directionConverter.convert(snakeI.getHeadDirection()))
                    .addAllPoints(snakeBodyConverter.convert(snakeI.getCoordinatesList()))
                    .setState(snakeStateConverter.convert(snakeI.getSnakeState()))
                    .setPlayerId(snakeI.getPlayerID())
                    .build());
        }
        return snakesProtoList;
    }

    @Override
    public List<SnakeI> inverseConvert(List<SnakesProto.GameState.Snake> protoSnakesList) throws ConvertionExeption {
        List<SnakeI> snakesList = new ArrayList<>();
        for(SnakesProto.GameState.Snake snake : protoSnakesList){
            snakesList.add(new Snake(snakeBodyConverter.inverseConvert(snake.getPointsList()),
                    snake.getPlayerId()));
        }
        return snakesList;
    }
}
