package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Coordinates;
import model.game.PointType;
import model.snakeGame.Point;
import model.utils.Converter;
import model.utils.ConvertionExeption;

import java.util.ArrayList;
import java.util.List;

public class SnakeBodyConverter implements Converter<List<Coordinates>, List<SnakesProto.GameState.Coord>> {
    private Converter<Coordinates, SnakesProto.GameState.Coord> coordinatesConverter =
            new CoordinatesConverter();

    @Override
    public List<SnakesProto.GameState.Coord> convert(List<Coordinates> coordinatesList)
            throws ConvertionExeption {
        List<SnakesProto.GameState.Coord> coordList = new ArrayList<>();
        Coordinates prevCoordinate = coordinatesList.get(0);
        coordList.add(coordinatesConverter.convert(prevCoordinate));
        for(int i = 1; i < coordinatesList.size(); i++){
            Coordinates curCoordinate = coordinatesList.get(i);
            coordList.add(SnakesProto.GameState.Coord.newBuilder()
                    .setX(curCoordinate.getX() - prevCoordinate.getX())
                    .setY(curCoordinate.getY() - prevCoordinate.getY())
                    .build());
            prevCoordinate = curCoordinate;
        }
        return coordList;
    }

    @Override
    public List<Coordinates> inverseConvert(List<SnakesProto.GameState.Coord> protoCoordinatesList) {
        List<Coordinates> coordinatesList = new ArrayList<>();
        SnakesProto.GameState.Coord curCoord = protoCoordinatesList.get(0);
        Coordinates prevPoint = new Point(curCoord.getX(), curCoord.getY(), PointType.SNAKE_BODY);
        coordinatesList.add(prevPoint);
        for(int i = 1; i < protoCoordinatesList.size(); i++){
            curCoord = protoCoordinatesList.get(i);
            Coordinates tempPoint = new Point(prevPoint.getX() + curCoord.getX(),
                    prevPoint.getY() + curCoord.getY(), PointType.SNAKE_BODY);
            coordinatesList.add(tempPoint);
            prevPoint = tempPoint;
        }
        return coordinatesList;
    }
}
