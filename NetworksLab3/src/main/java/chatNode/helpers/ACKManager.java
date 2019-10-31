package chatNode.helpers;

import node.MessagesNode;
import node.Node;
import node.NodeInfo;
import node.nodeMessages.NodeMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//or Runnable?
/*
просматривает, какие ack получены. если ack не получен,
то повторно отправляется сообщение
 */
public class ACKManager extends TimerTask {
    //private List<ACKNodesElement> ackNodesList = new CopyOnWriteArrayList<>();
    //private Queue<NodeMessage> sentMessagesQueue;
    private Map<NodeMessage, List<NodeInfo>> messageNodesListMap = new ConcurrentHashMap<>();
    private Map<NodeMessage, NodeInfo> messageNodeMap = new ConcurrentHashMap<>();
    private MessagesNode chatNode;

    public ACKManager(MessagesNode node){
        chatNode = node;
    }

    @Override
    public void run() {
        for(Map.Entry<NodeMessage, NodeInfo> messageNodeInfoEntry : messageNodeMap.entrySet()){
            chatNode.sendMessage(messageNodeInfoEntry.getKey(), messageNodeInfoEntry.getValue());
        }
        for(Map.Entry<NodeMessage, List<NodeInfo>> messageListEntry : messageNodesListMap.entrySet()){
            chatNode.sendMessage(messageListEntry.getKey(), messageListEntry.getValue());
        }
    }

    public void addACKRequest(NodeMessage message, List<NodeInfo> nodeInfoList){
        //ackNodesList.add(new ACKNodesElement(message, nodeInfoList));
    }

    public void ackRecieved(String messageUUID, String ip, int port){

    }

    public void addMessage(NodeMessage message, NodeInfo nodeInfo){
        messageNodeMap.put(message, nodeInfo);
    }

    public void addMessage(NodeMessage message, List<NodeInfo> nodeInfoList){
        List<NodeInfo> nodesList = new ArrayList<>();
        Collections.copy(nodesList, nodeInfoList);
        messageNodesListMap.put(message, nodesList);
    }
}

/*
class Pair<K,V>{
    private K key;
    private V value;

    Pair(K key, V value){
        this.key = key;
        this.value = value;
    }


}
*/

/*
class ACKNodesElement{
    //private String uuid;
    private NodeMessage message;
    private List<BasicNodeInfo> needACKNodesList;

    ACKNodesElement(NodeMessage message, List<NodeInfo> nodesInfoList){
        this.message = message;
       // needACKNodesList = nodesInfoList.subList(0, nodesInfoList.size());
    }

   // boolean hasNodes(){
     //   return !needACKNodesList.isEmpty();
    //}

   // NodeMessage

    List<NodeInfo> getNodesList(){
       // return needACKNodesList;
        return null;
    }

    void removeNodeInfo(NodeInfo nodeInfo){
        if(needACKNodesList.contains(nodeInfo)){
            needACKNodesList.remove(nodeInfo);
        }
    }
}
*/