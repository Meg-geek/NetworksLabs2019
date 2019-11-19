package model.snakeGameNetwork;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.Coordinates;
import model.game.Direction;
import model.game.PointType;
import model.game.SnakeI;
import model.snakeGame.Point;
import model.snakeGame.SnakeRecv;

import java.util.ArrayList;
import java.util.List;

public class GameStateMessage {
    private List<Coordinates> foodCoordsList;
    private int stateOrder;
    private List<SnakeI> snakesList;

    public GameStateMessage(SnakesProto.GameState message){
        this.foodCoordsList = makeCoordinatesList(message.getFoodsList(), PointType.FOOD);
        this.stateOrder = message.getStateOrder();
        makeSnakesList(message);
    }

    private void makeSnakesList(SnakesProto.GameState message){
        List<SnakesProto.GameState.Snake> snakesFromMsgList = message.getSnakesList();
        for(SnakesProto.GameState.Snake snake : snakesFromMsgList){
            snakesList.add(new SnakeRecv(makeCoordinatesList(snake.getPointsList(), PointType.SNAKE_BODY),
                    getDirection(snake.getHeadDirection()),
                    snake.getPlayerId()));
        }
    }

    private List<Coordinates> makeCoordinatesList(List<SnakesProto.GameState.Coord> coordList, PointType pointType){
        List<Coordinates> coordinatesList = new ArrayList<>();
        for(SnakesProto.GameState.Coord coord : coordList){
            coordinatesList.add(new Point(coord.getX(), coord.getY(), pointType));
        }
        return coordinatesList;
    }

    private Direction getDirection(SnakesProto.Direction protoDirection){
        switch(protoDirection){
            case UP:
                return Direction.UP;
            case RIGHT:
                return Direction.RIGHT;
            case LEFT:
                return Direction.LEFT;
            case DOWN:
                return Direction.DOWN;
            default:
                return Direction.UP;
        }
    }
}
