package model.snakeGameNetwork.messages;

import me.ippolitov.fit.snakes.SnakesProto;
import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;
import model.networkUtils.NodeRole;

public class RoleChangeMessage extends Message {
    private NodeRole senderRole, recieverRole;

    public RoleChangeMessage(BasicMessageInfo messageInfo, SnakesProto.GameMessage.RoleChangeMsg roleChangeMsg){
        super(messageInfo);
        senderRole = getRole(roleChangeMsg.getSenderRole());
        recieverRole = getRole(roleChangeMsg.getReceiverRole());
    }

    public RoleChangeMessage(BasicMessageInfo messageInfo, NodeRole senderRole, NodeRole recieverRole){
        super(messageInfo);
        this.senderRole = senderRole;
        this.recieverRole = recieverRole;
    }

    private NodeRole getRole(SnakesProto.NodeRole role){
        switch (role){
            case DEPUTY:
                return NodeRole.DEPUTY;
            case MASTER:
                return NodeRole.MASTER;
            case NORMAL:
                return NodeRole.NORMAL;
            case VIEWER:
                return NodeRole.VIEWER;
        }
        return null;
    }

    @Override
    public MessageType getType() {
        return MessageType.ROLE_CHANGE;
    }

    public NodeRole getSenderRole(){
        return senderRole;
    }

    public NodeRole getRecieverRole() {
        return recieverRole;
    }
}
