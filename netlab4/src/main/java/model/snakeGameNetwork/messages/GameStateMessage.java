package model.snakeGameNetwork.messages;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.*;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.MasterNode;
import model.networkUtils.Message;
import model.networkUtils.MessageType;
import model.snakeGame.Point;
import model.snakeGame.SnakeGamePlayer;
import model.snakeGame.SnakeRecv;

import java.util.ArrayList;
import java.util.List;

public class GameStateMessage extends Message {
    private List<Coordinates> foodCoordsList;
    private int stateOrder;
    private List<SnakeI> snakesList = new ArrayList<>();
    private List<Player> playersList = new ArrayList<>();
    //private List<NetworkUser> nodesList;
    private MasterNode masterNode;

    public GameStateMessage(BasicMessageInfo messageInfo, SnakesProto.GameState message){
        super(messageInfo);
        this.foodCoordsList = makeCoordinatesList(message.getFoodsList(), PointType.FOOD);
        this.stateOrder = message.getStateOrder();
        makeSnakesList(message);
        makePlayersList(message);
    }

    private void makePlayersList(SnakesProto.GameState message){
        List<SnakesProto.GamePlayer> gamePlayersList = message.getPlayers().getPlayersList();
        for(SnakesProto.GamePlayer player : gamePlayersList){
            playersList.add(new SnakeGamePlayer(player.getId(), player.getName(), player.getScore()));
        }
    }

    public List<Coordinates> getFoodCoordsList(){
        return foodCoordsList;
    }

    public List<SnakeI> getSnakesList(){
        return snakesList;
    }

    public int getStateOrder(){
        return stateOrder;
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

    @Override
    public MessageType getType() {
        return MessageType.STATE;
    }
}
