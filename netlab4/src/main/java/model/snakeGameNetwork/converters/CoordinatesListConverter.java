package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Coordinates;
import model.utils.Converter;
import model.utils.ConvertionExeption;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesListConverter implements Converter<List<Coordinates>, List<SnakesProto.GameState.Coord>> {
    private Converter<Coordinates, SnakesProto.GameState.Coord> coordinatesConverter = new CoordinatesConverter();

    @Override
    public List<SnakesProto.GameState.Coord> convert(List<Coordinates> coordinatesList) throws ConvertionExeption {
        List<SnakesProto.GameState.Coord> coordList = new ArrayList<>();
        for(Coordinates coordinates: coordinatesList){
            coordList.add(coordinatesConverter.convert(coordinates));
        }
        return coordList;
    }

    @Override
    public List<Coordinates> inverseConvert(List<SnakesProto.GameState.Coord> coordList) throws ConvertionExeption {
        List<Coordinates> coordinatesList = new ArrayList<>();
        for(SnakesProto.GameState.Coord coord : coordList){
            coordinatesList.add(coordinatesConverter.inverseConvert(coord));
        }
        return coordinatesList;
    }
}
