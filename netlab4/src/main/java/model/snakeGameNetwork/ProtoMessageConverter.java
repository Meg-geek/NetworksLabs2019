package model.snakeGameNetwork;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.*;
import model.networkUtils.Message;
import model.networkUtils.MessageConverter;
import model.networkUtils.NodeRole;
import model.snakeGame.Settings;
import model.snakeGameNetwork.converters.*;
import model.snakeGameNetwork.messages.*;
import model.utils.Converter;
import model.utils.ConvertionExeption;

import java.util.List;

public class ProtoMessageConverter implements MessageConverter {
    static final int MAX_MSG_SIZE;

    private Converter<List<SnakeI>, List<SnakesProto.GameState.Snake>> snakesListConverter
            = new SnakesConverter();
    private Converter<List<Coordinates>, List<SnakesProto.GameState.Coord>> coordinatesListConverter
            = new CoordinatesListConverter();
    private Converter<List<SnakeGamePlayerI>, SnakesProto.GamePlayers> playersListConverter
            = new PlayersConverter();
    private Converter<Settings, SnakesProto.GameConfig> gameConfigConverter
            = new GameConfigConverter();
    private Converter<Direction, SnakesProto.Direction> directionConverter
            = new DirectionConverter();
    private Converter<NodeRole, SnakesProto.NodeRole> nodeRoleConverter
            = new NodeRoleConverter();
    private Converter<Coordinates, SnakesProto.GameState.Coord> coordinatesConverter
            = new CoordinatesConverter();

    static{
        MAX_MSG_SIZE = GameSettings.fieldHeight.getDefaultValue() * GameSettings.fieldWidth.getDefaultValue()/25
                        * 200;
    }

    @Override
    public Message protoToMessage(SnakesProto.GameMessage message) throws ConvertionExeption{
        switch(message.getTypeCase()){
            case ACK:
                return new ACKMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getReceiverId());
            case JOIN:
                return new JoinMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getReceiverId(),
                        message.getJoin().getName());
            case PING:
                return new PingMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getReceiverId());
            case ERROR:
                return new ErrorMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getReceiverId(),
                        message.getError().getErrorMessage());
            case STATE:
                return new GameStateMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getState().getState().getStateOrder(),
                        snakesListConverter.inverseConvert(message.getState().getState().getSnakesList()),
                        coordinatesListConverter.inverseConvert(message.getState().getState().getFoodsList()),
                        playersListConverter.inverseConvert(message.getState().getState().getPlayers()),
                        gameConfigConverter.inverseConvert(message.getState().getState().getConfig())
                );
            case STEER:
                return new SteerMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getReceiverId(),
                        directionConverter.inverseConvert(message.getSteer().getDirection()));
            case ROLE_CHANGE:
                return new RoleChangeMessage(message.getMsgSeq(),
                        message.getSenderId(),
                        message.getReceiverId(),
                        nodeRoleConverter.inverseConvert(message.getRoleChange().getSenderRole()),
                        nodeRoleConverter.inverseConvert(message.getRoleChange().getReceiverRole())
                        );
            case ANNOUNCEMENT:
                return new AnnouncementMessage(message.getMsgSeq(), message.getSenderId(),
                        gameConfigConverter.inverseConvert(message.getAnnouncement().getConfig()),
                        playersListConverter.inverseConvert(message.getAnnouncement().getPlayers()),
                        message.getAnnouncement().getCanJoin());
            case TYPE_NOT_SET:
                return null;
        }
        return null;
    }

    @Override
    public SnakesProto.GameMessage messageToProto(Message message) throws ConvertionExeption{
        switch(message.getType()){
            case STATE:
                if(message instanceof GameStateMessage){
                    GameStateMessage gameStateMessage = (GameStateMessage)message;
                    return getGameMessageBuilder(gameStateMessage)
                            .setState(SnakesProto.GameMessage.StateMsg
                                    .newBuilder()
                                    .setState(SnakesProto.GameState
                                            .newBuilder()
                                            .setConfig(gameConfigConverter
                                                    .convert(gameStateMessage.getGameSettings()))
                                            .setPlayers(playersListConverter
                                                    .convert(gameStateMessage.getSnakeGamePlayersList()))
                                            .setStateOrder(gameStateMessage.getStateOrder())
                                            .addAllFoods(coordinatesListConverter.convert(gameStateMessage.getFoodCoordsList()))
                                            .addAllSnakes(snakesListConverter.convert(gameStateMessage.getSnakesList()))
                                            .build())
                                    .build())
                            .build();
                }
                break;
            case ACK:
                if(message instanceof ACKMessage){
                    return getGameMessageBuilder(message)
                            .setAck(SnakesProto.GameMessage.AckMsg.getDefaultInstance())
                            .build();
                }
                break;
            case JOIN:
                if(message instanceof JoinMessage){
                    JoinMessage joinMessage = (JoinMessage)message;
                    return getGameMessageBuilder(message)
                            .setJoin(SnakesProto.GameMessage.JoinMsg
                                    .newBuilder()
                                    .setName(joinMessage.getPlayerName())
                                    .build())
                            .build();
                }
                break;
            case ERROR:
                if(message instanceof ErrorMessage){
                    ErrorMessage errorMessage = (ErrorMessage)message;
                    return getGameMessageBuilder(errorMessage)
                            .setError(SnakesProto.GameMessage.ErrorMsg
                                    .newBuilder()
                                    .setErrorMessage(errorMessage.getErrorMessage())
                                    .build())
                            .build();
                }
                break;
            case STEER:
                if(message instanceof SteerMessage){
                    SteerMessage steerMessage = (SteerMessage)message;
                    return getGameMessageBuilder(message)
                            .setSteer(SnakesProto.GameMessage.SteerMsg
                                    .newBuilder()
                                    .setDirection(directionConverter
                                            .convert(steerMessage.getDirection()))
                                    .build())
                            .build();
                }
                break;
            case ROLE_CHANGE:
                if(message instanceof RoleChangeMessage){
                    RoleChangeMessage roleChangeMessage = (RoleChangeMessage)message;
                    SnakesProto.GameMessage.RoleChangeMsg.Builder roleChangeMessageBuilder = SnakesProto.GameMessage.RoleChangeMsg
                            .newBuilder();
                    SnakesProto.NodeRole nodeRole = nodeRoleConverter
                            .convert(roleChangeMessage.getSenderRole());
                    if(nodeRole != null){
                        roleChangeMessageBuilder.setSenderRole(nodeRole);
                        nodeRole = nodeRoleConverter
                                .convert(roleChangeMessage.getRecieverRole());
                    }
                    if(nodeRole != null){
                        roleChangeMessageBuilder.setReceiverRole(nodeRole);
                    }
                    return getGameMessageBuilder(message)
                            .setRoleChange(
                                    roleChangeMessageBuilder
                                    .build())
                            .build();
                }
                break;
            case PING:
                if(message instanceof PingMessage){
                    return getGameMessageBuilder(message)
                            .setPing(SnakesProto.GameMessage.PingMsg.getDefaultInstance())
                            .build();
                }
                break;
            case ANNOUNCEMENT:
                if(message instanceof AnnouncementMessage){
                    AnnouncementMessage announcementMessage = (AnnouncementMessage)message;
                    return getGameMessageBuilder(announcementMessage)
                            .setAnnouncement(SnakesProto.GameMessage.AnnouncementMsg
                                    .newBuilder()
                                    .setCanJoin(announcementMessage.isJoinable())
                                    .setPlayers(playersListConverter.convert(announcementMessage.getPlayersList()))
                                    .setConfig(gameConfigConverter.convert(announcementMessage.getGameSettings()))
                                    .build())
                            .build();
                }
                break;
        }
        return null;
    }

    private SnakesProto.GameMessage.Builder getGameMessageBuilder(Message message){
        return SnakesProto.GameMessage.newBuilder()
                .setSenderId(message.getSenderID())
                .setReceiverId(message.getReceiverID())
                .setMsgSeq(message.getNumber());
    }
}
