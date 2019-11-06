package chatNode.helpers;

import chatNode.ChatNode;
import chatNode.ChatNodeMessage;
import node.Node;
import node.NodeInfo;
import node.NodeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectionController implements Runnable {
    private List<NodeInfo> nodesInfoList;
    private ChatNode node;

    public ConnectionController(List<NodeInfo> nodesInfoList, ChatNode node){
        this.nodesInfoList = nodesInfoList;
        this.node = node;
    }

    @Override
    public void run() {
        if(nodesInfoList.size() == 0){
            return;
        }
        List<NodeInfo> nodeInfoRemoveList = new ArrayList<>();
        long nowTime = new Date().getTime();
        for(NodeInfo nodeInfo : nodesInfoList){
            if(nowTime - nodeInfo.getLastActivity().getTime() > Node.TIMEOUT_MILSEC){
                if(nodeInfo.getParentAddress() == null ||
                        !nodeInfo.replaceNode()
                    ){
                    nodeInfoRemoveList.add(nodeInfo);
                }
            }
        }
        NodeInfo alternativeNode = node.getAlernativeNode();
        for(NodeInfo nodeInfo : nodeInfoRemoveList){
            if(nodeInfo == alternativeNode){
                alternativeNode = null;
            }
            nodesInfoList.remove(nodeInfo);
        }
        if(nodesInfoList.size() > 0){
            sendChecks();
            if(alternativeNode == null){
                node.setAlernativeNode(nodesInfoList.get(0));
            }
        } else {
            node.setAlernativeNode(null);
        }
    }

    private void sendChecks(){
        try{
            node.sendMessage(new ChatNodeMessage(NodeMessage.PERIOD_CHECK));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
