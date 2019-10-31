package chatNode.helpers;

import node.Node;
import node.NodeInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class ConnectionController extends TimerTask {
    private List<NodeInfo> nodesInfoList;

    public ConnectionController(List<NodeInfo> nodesInfoList){
        this.nodesInfoList = nodesInfoList;
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
    }
}
