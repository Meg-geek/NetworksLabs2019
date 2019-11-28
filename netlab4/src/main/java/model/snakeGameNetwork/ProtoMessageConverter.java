package model.snakeGameNetwork;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.*;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageConverter;
import model.networkUtils.NodeRole;
import model.snakeGameNetwork.messages.*;

import java.util.ArrayList;
import java.util.List;

public class ProtoMessageConverter implements MessageConverter {
    public static final int MAX_MSG_SIZE;

    static{
        MAX_MSG_SIZE = GameSettings.fieldHeight.getDefaultValue() * GameSettings.fieldWidth.getDefaultValue()/25
                        * 200;
    }

    @Override
    public Message protoToMessage(SnakesProto.GameMessage message) {
        BasicMessageInfo basicMessageInfo = new BasicMessageInfo(message.getMsgSeq(),
                message.getSenderId(), message.getReceiverId());
        switch(message.getTypeCase()){
            case ACK:
                return new ACKMessage(basicMessageInfo);
            case JOIN:
                return new JoinMessage(basicMessageInfo,
                        message.getJoin().getName());
            case PING:
                return new PingMessage(basicMessageInfo);
            case ERROR:
                return new ErrorMessage(basicMessageInfo,
                        message.getError().getErrorMessage());
            case STATE:
                return new GameStateMessage(basicMessageInfo,
                        message.getState().getState());
            case STEER:
                return new SteerMessage(basicMessageInfo,
                        message.getSteer().getDirection());
            case ROLE_CHANGE:
                return new RoleChangeMessage(basicMessageInfo, message.getRoleChange());
            case ANNOUNCEMENT:
                return new AnnouncmentMessage(basicMessageInfo, message.getAnnouncement());
            case TYPE_NOT_SET:
                //or log?
                System.out.println("Message type does not set");
                return null;
        }
        return null;
    }

    @Override
    public SnakesProto.GameMessage messageToProto(Message message) {
        switch(message.getType()){
            case STATE:
                if(message instanceof GameStateMessage){
                    return gameStateMessage((GameStateMessage)message);
                }
                break;


        }
        return null;
    }

    private SnakesProto.GameMessage gameStateMessage(GameStateMessage message){
        SnakesProto.GameState.Builder gameStateBuilder = SnakesProto.GameState.newBuilder()
                        .addAllFoods(toCoordsList(message.getFoodCoordsList()))
                        .addAllSnakes(toSnakeList(message.getSnakesList()))
                        .setStateOrder(message.getStateOrder())
                        .setPlayers(getGamePlayers(message.getSnakeGamePlayersList()))
                        .setConfig(SnakesProto.GameConfig.getDefaultInstance());

        SnakesProto.GameMessage.Builder gameMessageBuilder = SnakesProto.GameMessage.newBuilder();
        return gameMessageBuilder.setMsgSeq(message.getNumber())
                .setSenderId(message.getSenderID())
                .setReceiverId(message.getReceiverID())
                .setState(SnakesProto.GameMessage.StateMsg.newBuilder().setState(gameStateBuilder.build()).build())
                .build();
    }

    private SnakesProto.GamePlayers getGamePlayers(List<SnakeGamePlayerI> playersList){
        SnakesProto.GamePlayers.Builder playersBuilder= SnakesProto.GamePlayers.newBuilder();
        for(SnakeGamePlayerI player : playersList){
            SnakesProto.GamePlayer.Builder playerBuilder = SnakesProto.GamePlayer.newBuilder();
            playersBuilder.addPlayers(playerBuilder.setId(player.getID())
                                                        .setPort(player.getPort())
                                                        .setRole(getRole(player.getRole()))
                                                        .setScore(player.getScore())
                                                        .setType(SnakesProto.PlayerType.HUMAN)
                                                        .setIpAddress(player.getIP())
                                                        .setName(player.getName()).build());
        }
        return playersBuilder.build();
    }


    private SnakesProto.NodeRole getRole(NodeRole role){
        switch(role){
            case VIEWER:
                return SnakesProto.NodeRole.VIEWER;
            case NORMAL:
                return SnakesProto.NodeRole.NORMAL;
            case MASTER:
                return SnakesProto.NodeRole.MASTER;
            case DEPUTY:
                return SnakesProto.NodeRole.DEPUTY;
        }
        return null;
    }

    private List<SnakesProto.GameState.Snake> toSnakeList(List<SnakeI> snakesIList){
        List<SnakesProto.GameState.Snake> snakesList = new ArrayList<>();
        for(SnakeI snakeI : snakesIList){
            SnakesProto.GameState.Snake.Builder snakeBuilder = SnakesProto.GameState.Snake.newBuilder();
            snakesList.add(snakeBuilder.setHeadDirection(toDirection(snakeI.getHeadDirection()))
                    .addAllPoints(toSnakeBodyList(snakeI.getCoordinatesList()))
                    .setState((snakeI.isAlive())? SnakesProto.GameState.Snake.SnakeState.ALIVE:
                            SnakesProto.GameState.Snake.SnakeState.ZOMBIE)
                    .setPlayerId(snakeI.getPlayerID())
                    .build());
        }
        return snakesList;
    }

    private List<SnakesProto.GameState.Coord> toSnakeBodyList(List<Coordinates> snakeCoordinatesList){
        List<SnakesProto.GameState.Coord> coordList = new ArrayList<>();
        Coordinates prevCoordinate = snakeCoordinatesList.get(0);
        coordList.add(SnakesProto.GameState.Coord.newBuilder().setX(prevCoordinate.getX())
                .setY(prevCoordinate.getY()).build());
        for(int i = 1; i < snakeCoordinatesList.size(); i++){
            Coordinates curCoordinate = snakeCoordinatesList.get(i);
            coordList.add(SnakesProto.GameState.Coord.newBuilder()
                    .setX(curCoordinate.getX() - prevCoordinate.getX())
                    .setY(curCoordinate.getY() - prevCoordinate.getY())
                    .build());
            prevCoordinate = curCoordinate;
        }
        return coordList;
    }

    private SnakesProto.Direction toDirection(Direction direction){
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
        return SnakesProto.Direction.UP;
    }

    private List<SnakesProto.GameState.Coord> toCoordsList(List<Coordinates> coordinatesList){
        List<SnakesProto.GameState.Coord> coordList = new ArrayList<>();
        for(Coordinates coordinates : coordinatesList){
            coordList.add(SnakesProto.GameState.Coord.newBuilder()
                            .setX(coordinates.getX())
                            .setY(coordinates.getY())
                            .build());
        }
        return coordList;
    }
}
