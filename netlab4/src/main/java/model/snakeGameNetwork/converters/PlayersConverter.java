package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.game.SnakeGamePlayerI;
import model.networkUtils.NodeRole;
import model.snakeGame.SnakeGamePlayer;
import model.utils.Converter;
import model.utils.ConvertionExeption;

import java.util.ArrayList;
import java.util.List;

public class PlayersConverter implements Converter<List<SnakeGamePlayerI>, SnakesProto.GamePlayers> {
    private Converter<NodeRole, SnakesProto.NodeRole> roleConverter = new NodeRoleConverter();

    @Override
    public SnakesProto.GamePlayers convert(List<SnakeGamePlayerI> playersList) throws ConvertionExeption{
        SnakesProto.GamePlayers.Builder playersBuilder= SnakesProto.GamePlayers.newBuilder();
        for(SnakeGamePlayerI player : playersList){
            SnakesProto.GamePlayer.Builder playerBuilder = SnakesProto.GamePlayer.newBuilder();
            playersBuilder.addPlayers(playerBuilder.setId(player.getID())
                    .setPort(player.getPort())
                    .setRole(roleConverter.convert(player.getRole()))
                    .setScore(player.getScore())
                    .setType(SnakesProto.PlayerType.HUMAN)
                    .setIpAddress(player.getIP())
                    .setName(player.getName()).build());
        }
        return playersBuilder.build();
    }

    @Override
    public List<SnakeGamePlayerI> inverseConvert(SnakesProto.GamePlayers protoGamePlayers) throws ConvertionExeption {
        List<SnakesProto.GamePlayer> gamePlayersList = protoGamePlayers.getPlayersList();
        List<SnakeGamePlayerI> snakeGamePlayersList = new ArrayList<>();
        for(SnakesProto.GamePlayer player : gamePlayersList){
            SnakeGamePlayerI snakeGamePlayer = new SnakeGamePlayer(player.getId(),
                    player.getName(),
                    player.getScore(),
                    player.getIpAddress(),
                    player.getPort(),
                    roleConverter.inverseConvert(player.getRole())
                    );
            snakeGamePlayersList.add(snakeGamePlayer);
        }
        return snakeGamePlayersList;
    }
}
