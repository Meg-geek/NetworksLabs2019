package chatNode.helpers;

import node.NodeInfo;
import node.NodeMessage;

import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageSender {
    private DatagramSocket socket;
    private ExecutorService sendThreads;

    public MessageSender(DatagramSocket socket, int threadsAmount){
        this.socket = socket;
        sendThreads = Executors.newFixedThreadPool(threadsAmount);
    }

    public void sendMessage(NodeMessage message, List<NodeInfo> nodesList){
        sendThreads.submit(new SendTask(socket, message, nodesList));
    }

    public void sendMessage(NodeMessage message, NodeInfo nodeInfo){
        sendThreads.submit(new SendTask(socket, message, nodeInfo));
    }
}
