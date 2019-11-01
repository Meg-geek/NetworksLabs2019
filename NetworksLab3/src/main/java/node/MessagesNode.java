package node;

import java.util.List;

public interface MessagesNode extends Node {
    void sendMessage(NodeMessage message, NodeInfo nodeInfo);
    void sendMessage(NodeMessage message, List<NodeInfo> nodesInfoList);
    void sendMessage(NodeMessage message);
    void addRecvMessage(NodeMessage message);
}
