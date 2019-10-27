package chatNode;

import node.Node;
import node.NodeInfo;
import node.nodeMessages.AlternativeNodeMessage;
import node.nodeMessages.ConnectionNodeMessage;
import node.nodeMessages.NodeMessage;
import node.nodeMessages.TextNodeMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatNode implements Node {
    private String nodeName;
    private int lossPerc;
    private DatagramSocket socket;
    private List<NodeInfo> nearNodesInfoList;
    private NodeInfo alernativeNode;
    private Queue<NodeMessage> sentMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private Queue<NodeMessage> recvMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private Queue<String> printMessagesUUIDQueue = new ArrayDeque<>(MESSAGE_CAPACITY);
    private final int THREADS_AMOUNT = 3;
    private ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_AMOUNT);

    public ChatNode(String nodeName, int port, int lossPerc) throws SocketException {
        this.nodeName = nodeName;
        this.lossPerc = lossPerc;
        socket = new DatagramSocket(port);
        nearNodesInfoList = new CopyOnWriteArrayList<>();
    }

    public ChatNode(String nodeName, int port, int lossPerc, String parentIP, int parentPort) throws IOException {
        this(nodeName, port, lossPerc);
        alernativeNode = new ChatNodeInfo(parentIP, parentPort);
        nearNodesInfoList.add(alernativeNode);
        //byte[] buf = BigInteger.valueOf(NodeMessage.CONNECTION).toByteArray();
        //socket.send(new DatagramPacket(buf, buf.length,  alernativeNode.getInetAddress(), alernativeNode.getPort()));
        //System.out.println("length " + buf.length + " " + BigInteger.valueOf(buf[0]));
    }

    //private void recieveMessages(){ }

    public void start() {

    }

    public void addRecvMessage(NodeMessage message){
        if(recvMessageQueue.size() == MESSAGE_CAPACITY){
            recvMessageQueue.poll();
        }
        recvMessageQueue.add(message);
        switch(message.getMessageType()){
            case NodeMessage.CONNECTION:
                addNode(((ConnectionNodeMessage)message).getIP(),
                        ((ConnectionNodeMessage)message).getPort());
                break;
            case NodeMessage.TEXT:
                printMessage((TextNodeMessage) message);
                break;
            case NodeMessage.ACK:
                break;
            case NodeMessage.ALTERNATIVE:
                setNodeParent(((AlternativeNodeMessage)message).getIP(),
                        ((AlternativeNodeMessage)message).getParentIP(),
                        ((AlternativeNodeMessage)message).getParentPort());
                break;
            case NodeMessage.PERIOD_CHECK:
                refreshNodeActivity(((ConnectionNodeMessage)message).getIP());
                break;
        }
    }

    private void printMessage(TextNodeMessage message){
        String uuid = message.getUUID();
        if(!printMessagesUUIDQueue.contains(uuid)){
            System.out.println(message.getText());
            if(printMessagesUUIDQueue.size() >= MESSAGE_CAPACITY){
                printMessagesUUIDQueue.poll();
            }
            printMessagesUUIDQueue.add(uuid);
        }
    }

    private void addNode(String ip, int port) {
        try{
            NodeInfo nodeInfo = new ChatNodeInfo(ip, port);
            nearNodesInfoList.add(nodeInfo);
        } catch(UnknownHostException ex){
            //log exception?
        }
    }

    private void setNodeParent(String ip, String parentIP, int parentPort){
        NodeInfo nodeInfo = null;
        Iterator<NodeInfo> iterator = nearNodesInfoList.iterator();
        while(iterator.hasNext() && nodeInfo == null){
            if (iterator.next().getInetAddress().getHostAddress().equals(ip)){
                nodeInfo = iterator.next();
            }
        }
        try{
            nodeInfo.setParent(parentIP, parentPort);
        } catch(UnknownHostException ex) {
            //log?
        }
    }

    private void refreshNodeActivity(String ip){
        Iterator<NodeInfo> iterator = nearNodesInfoList.iterator();
        while(iterator.hasNext() && !iterator.next().getInetAddress().getHostAddress().equals(ip)){
        }
        iterator.next().refreshActivity();
    }

    int getLossPerc(){
        return lossPerc;
    }
}
