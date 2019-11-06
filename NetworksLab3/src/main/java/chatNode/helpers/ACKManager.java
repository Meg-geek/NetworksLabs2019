package chatNode.helpers;

import node.MessagesNode;
import node.NodeInfo;
import node.NodeMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
просматривает, какие ack получены. если ack не получен,
то повторно отправляется сообщение
 */
public class ACKManager implements Runnable {
    private Map<NodeMessage, List<NodeInfo>> messageNodesListMap = new ConcurrentHashMap<>();
    private Map <String, NodeMessage> uuidMessage = new ConcurrentHashMap<>();
    private MessagesNode chatNode;

    public ACKManager(MessagesNode node){
        chatNode = node;
    }

    @Override
    public void run() {
        List<NodeMessage> messagesList = new ArrayList<>();
        for(Map.Entry<NodeMessage, List<NodeInfo>> messageListEntry : messageNodesListMap.entrySet()){
            List<NodeInfo> nodeInfoList = messageListEntry.getValue();
            refreshNodeInfoList(nodeInfoList);
            if(nodeInfoList.size() > 0){
                chatNode.sendMessage(messageListEntry.getKey(), nodeInfoList);
            } else {
                messagesList.add(messageListEntry.getKey());
            }
        }
        for(NodeMessage message : messagesList){
            messageNodesListMap.remove(message);
            uuidMessage.remove(message.getUUID());
        }
    }

    private void refreshNodeInfoList(List<NodeInfo> nodeInfoList){
        long nowTime = new Date().getTime();
        List<NodeInfo> nodesToRemove = new ArrayList<>();
        for(NodeInfo nodeInfo: nodeInfoList){
            if(nowTime - nodeInfo.getLastActivity().getTime() > MessagesNode.TIMEOUT_MILSEC){
                nodesToRemove.add(nodeInfo);
            }
        }
        for(NodeInfo nodeInfoRemove : nodesToRemove){
            nodeInfoList.remove(nodeInfoRemove);
        }
    }

    public void ackRecieved(String messageUUID, NodeInfo nodeInfo){
        NodeMessage message = uuidMessage.get(messageUUID);
        if(message == null){
            return;
        }
        List<NodeInfo> nodeInfoList = messageNodesListMap.get(message);
        if(nodeInfoList != null){
            nodeInfoList.remove(nodeInfo);
        }
    }

    public void addMessage(NodeMessage message, NodeInfo nodeInfo){
        List<NodeInfo> nodeInfoList = new CopyOnWriteArrayList<>();
        nodeInfoList.add(nodeInfo);
        addMessage(message, nodeInfoList);
    }

    public void addMessage(NodeMessage message, List<NodeInfo> nodeInfoList){
        messageNodesListMap.put(message, nodeInfoList);
        uuidMessage.put(message.getUUID(), message);
    }
}
