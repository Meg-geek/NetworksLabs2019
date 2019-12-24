package model.snakeGameNetwork.converters;

import me.ippolitov.fit.snakes.SnakesProto;
import model.networkUtils.NodeRole;
import model.utils.Converter;
import model.utils.ConvertionExeption;

public class NodeRoleConverter implements Converter<NodeRole, SnakesProto.NodeRole> {
    @Override
    public SnakesProto.NodeRole convert(NodeRole role) throws ConvertionExeption {
        if(role == null){
            return null;
        }
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
        throw new ConvertionExeption("Wrong node role");
    }

    @Override
    public NodeRole inverseConvert(SnakesProto.NodeRole protoRole) throws ConvertionExeption {
        if(protoRole == null){
            return null;
        }
        switch (protoRole){
            case DEPUTY:
                return NodeRole.DEPUTY;
            case MASTER:
                return NodeRole.MASTER;
            case NORMAL:
                return NodeRole.NORMAL;
            case VIEWER:
                return NodeRole.VIEWER;
        }
        throw new ConvertionExeption("Wrong new proto role");
    }
}
