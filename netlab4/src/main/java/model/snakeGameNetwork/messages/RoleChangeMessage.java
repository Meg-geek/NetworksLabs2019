package model.snakeGameNetwork.messages;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.Message;
import model.networkUtils.MessageType;
import model.networkUtils.NodeRole;

public class RoleChangeMessage extends Message {
    private NodeRole senderRole, recieverRole;

    public RoleChangeMessage(long msgSeq, int senderId, int reciverId, NodeRole senderRole, NodeRole recieverRole){
        super(new BasicMessageInfo(msgSeq, senderId, reciverId));
        this.senderRole = senderRole;
        this.recieverRole = recieverRole;
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
