package node;

import node.nodeMessages.NodeMessage;

import java.util.List;

public interface MessagesNode extends Node {
    void sendMessage(NodeMessage message, NodeInfo nodeInfo);
    void sendMessage(NodeMessage message, List<NodeInfo> nodesInfoList);
    void addRecvMessage(NodeMessage message);
}
