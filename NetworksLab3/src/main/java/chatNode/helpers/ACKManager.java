package chatNode.helpers;

import node.MessagesNode;
import node.NodeInfo;
import node.NodeMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//or Runnable?
/*
просматривает, какие ack получены. если ack не получен,
то повторно отправляется сообщение
 */
public class ACKManager implements Runnable {
    private Map<NodeMessage, List<NodeInfo>> messageNodesListMap = new ConcurrentHashMap<>();
    private Map<NodeMessage, NodeInfo> messageNodeMap = new ConcurrentHashMap<>();
    private Map <String, NodeMessage> uuidMessage = new ConcurrentHashMap<>();
    private MessagesNode chatNode;

    public ACKManager(MessagesNode node){
        chatNode = node;
    }

    @Override
    public void run() {
        long nowTime = new Date().getTime();
        List<NodeMessage> messagesList = new ArrayList<>();
        for(Map.Entry<NodeMessage, NodeInfo> messageNodeInfoEntry : messageNodeMap.entrySet()){
            if(nowTime - messageNodeInfoEntry.getValue().getLastActivity().getTime() > MessagesNode.TIMEOUT_MILSEC){
                messagesList.add(messageNodeInfoEntry.getKey());
            } else {
                chatNode.sendMessage(messageNodeInfoEntry.getKey(), messageNodeInfoEntry.getValue());
            }
        }
        for(NodeMessage message : messagesList){
            messageNodeMap.remove(message);
            uuidMessage.remove(message.getUUID());
        }
        for(Map.Entry<NodeMessage, List<NodeInfo>> messageListEntry : messageNodesListMap.entrySet()){
            refreshNodeInfoList(messageListEntry.getValue());
            chatNode.sendMessage(messageListEntry.getKey(), messageListEntry.getValue());
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
        if(messageNodeMap.containsKey(message)) {
            messageNodeMap.remove(message);
            uuidMessage.remove(messageUUID);
        }
        if(messageNodesListMap.containsKey(message)){
            messageNodesListMap.get(message).remove(nodeInfo);
        }
    }

    public void addMessage(NodeMessage message, NodeInfo nodeInfo){
        messageNodeMap.put(message, nodeInfo);
        uuidMessage.put(message.getUUID(), message);
    }

    public void addMessage(NodeMessage message, List<NodeInfo> nodeInfoList){
        List<NodeInfo> nodesList = new ArrayList<>();
        Collections.copy(nodesList, nodeInfoList);
        messageNodesListMap.put(message, nodesList);
        uuidMessage.put(message.getUUID(), message);
    }
}
