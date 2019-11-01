package chatNode.helpers;

import node.NodeInfo;
import node.NodeMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class SendTask implements Runnable {
    private NodeMessage messageToSend;
    private DatagramSocket socket;
    private List<NodeInfo> nodesInfoList;

    SendTask(DatagramSocket socket, NodeMessage message, List<NodeInfo> nodesInfo){
            messageToSend = message;
            this.socket = socket;
            nodesInfoList = nodesInfo;
    }

    SendTask(DatagramSocket socket, NodeMessage message, NodeInfo nodeInfo){
        messageToSend = message;
        this.socket = socket;
        nodesInfoList = new ArrayList<>();
        nodesInfoList.add(nodeInfo);
    }

    @Override
    public void run() {
        try{
            for(NodeInfo nodeInfo : nodesInfoList){
                socket.send(new DatagramPacket(messageToSend.toBytes(), messageToSend.toBytes().length,
                        nodeInfo.getInetAddress(), nodeInfo.getPort()));
            }
        } catch(IOException ex){
            //throw new RuntimeException(ex); ?
            ex.printStackTrace();
        }
    }
}
