package chatNode.helpers;

import chatNode.ChatNodeMessage;
import node.MessagesNode;
import node.Node;
import node.NodeInfo;
import node.NodeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectionController implements Runnable {
    private List<NodeInfo> nodesInfoList;
    private MessagesNode node;

    public ConnectionController(List<NodeInfo> nodesInfoList, MessagesNode node){
        this.nodesInfoList = nodesInfoList;
        this.node = node;
    }

    @Override
    public void run() {
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
        for(NodeInfo nodeInfo : nodeInfoRemoveList){
            nodesInfoList.remove(nodeInfo);
        }
        sendChecks();
    }

    private void sendChecks(){
        try{
            node.sendMessage(new ChatNodeMessage(NodeMessage.PERIOD_CHECK));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
