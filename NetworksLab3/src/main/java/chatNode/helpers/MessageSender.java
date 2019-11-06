package chatNode.helpers;

import node.NodeInfo;
import node.NodeMessage;

import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageSender {
    private DatagramSocket socket;
    private ExecutorService sendThread;

    public MessageSender(DatagramSocket socket){
        this.socket = socket;
        sendThread = Executors.newSingleThreadExecutor();
    }

    public void sendMessage(NodeMessage message, List<NodeInfo> nodesList){
        sendThread.submit(new SendTask(socket, message, nodesList));
    }

    public void sendMessage(NodeMessage message, NodeInfo nodeInfo){
        sendThread.submit(new SendTask(socket, message, nodeInfo));
    }
}
