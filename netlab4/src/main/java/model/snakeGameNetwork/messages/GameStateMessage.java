package model.snakeGameNetwork.messages;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.*;
import model.networkUtils.*;
import model.snakeGame.Point;
import model.snakeGame.Snake;
import model.snakeGame.SnakeGamePlayer;
import model.snakeGameNetwork.SnakeNetworkUser;

import java.util.ArrayList;
import java.util.List;

public class GameStateMessage extends Message {
    private List<Coordinates> foodCoordsList;
    private int stateOrder;
    private List<SnakeI> snakesList = new ArrayList<>();
    private List<Player> playersList = new ArrayList<>();
    private List<NetworkUser> nodesList = new ArrayList<>();

    public GameStateMessage(BasicMessageInfo messageInfo, SnakesProto.GameState message){
        super(messageInfo);
        this.foodCoordsList = makeCoordinatesList(message.getFoodsList(), PointType.FOOD);
        this.stateOrder = message.getStateOrder();
        makeSnakesList(message);
        makePlayersList(message);
        makeUsersList(message);
    }

    private void makeUsersList(SnakesProto.GameState gameState){
        List<SnakesProto.GamePlayer> gamePlayersList = gameState.getPlayers().getPlayersList();
        for(SnakesProto.GamePlayer player : gamePlayersList){
            NodeRole role = getRole(player.getRole());
            nodesList.add(new SnakeNetworkUser(player.getId(), role,
                    player.getIpAddress(), player.getPort()));
        }
    }

    private NodeRole getRole(SnakesProto.NodeRole protoRole){
        switch (protoRole){
            case VIEWER:
                return NodeRole.VIEWER;
            case NORMAL:
                return NodeRole.NORMAL;
            case MASTER:
                return NodeRole.MASTER;
            case DEPUTY:
                return NodeRole.DEPUTY;
            default:
                return null;
        }
    }

    private void makePlayersList(SnakesProto.GameState gameState){
        List<SnakesProto.GamePlayer> gamePlayersList = gameState.getPlayers().getPlayersList();
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
            snakesList.add(new Snake(makeSnakeBody(snake.getPointsList()),
                    getDirection(snake.getHeadDirection()),
                    snake.getPlayerId()));
        }
    }

    private List<Coordinates> makeSnakeBody(List<SnakesProto.GameState.Coord> coordList){
        List<Coordinates> coordinatesList = new ArrayList<>();
        SnakesProto.GameState.Coord curCoord = coordList.get(0);
        Coordinates prevPoint = new Point(curCoord.getX(), curCoord.getY(), PointType.SNAKE_BODY);
        coordinatesList.add(prevPoint);
        for(int i = 1; i < coordList.size(); i++){
            curCoord = coordList.get(i);
            Coordinates tempPoint = new Point(prevPoint.getX() + curCoord.getX(),
                    prevPoint.getY() + curCoord.getY(), PointType.SNAKE_BODY);
            coordinatesList.add(tempPoint);
            prevPoint = tempPoint;
        }
        return coordinatesList;
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

    public List<NetworkUser> getNodesList() {
        return nodesList;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }
}
