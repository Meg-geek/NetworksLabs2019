package chatNode;

import chatNode.chatNodeMessages.ChatNodeMessage;
import node.NodeInfo;
import node.nodeMessages.NodeMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class SendTask implements Runnable {
    private NodeMessage messageToSend;
    private DatagramSocket socket;
    private List<NodeInfo> nodesInfoList;

    public SendTask(DatagramSocket socket, NodeMessage message, List<NodeInfo> nodesInfo){
            messageToSend = message;
            this.socket = socket;
            nodesInfoList = nodesInfo;
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
