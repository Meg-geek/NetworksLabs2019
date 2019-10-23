package chatNode;

import node.Node;
import node.NodeMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatNode implements Node {
    private String nodeName;
    private int lossPerc;
    private DatagramSocket socket;
    private List<ChatNodeInfo> nearNodesInfoList;
    private ChatNodeInfo alernativeNode;
    private Queue<NodeMessage> sentMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private Queue<NodeMessage> recvMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private final int THREADS_AMOUNT = 3;
    private ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_AMOUNT);

    public ChatNode(String nodeName, int port, int lossPerc) throws SocketException {
        this.nodeName = nodeName;
        this.lossPerc = lossPerc;
        socket = new DatagramSocket(port);
        nearNodesInfoList = new LinkedList<>();
    }

    public ChatNode(String nodeName, int port, int lossPerc, String parentIP, int parentPort) throws IOException {
        this(nodeName, port, lossPerc);
        alernativeNode = new ChatNodeInfo(parentIP, parentPort);
        nearNodesInfoList.add(alernativeNode);
        //byte[] buf = BigInteger.valueOf(NodeMessage.CONNECTION).toByteArray();
        //socket.send(new DatagramPacket(buf, buf.length,  alernativeNode.getInetAddress(), alernativeNode.getPort()));
        //System.out.println("length " + buf.length + " " + BigInteger.valueOf(buf[0]));
    }

    private void recieveMessages(){

    }

    public void start() {

    }

    public void addRecvMessage(NodeMessage message){
        if(recvMessageQueue.size() == MESSAGE_CAPACITY){
            recvMessageQueue.poll();
        }
        recvMessageQueue.add(message);
        switch(message.getMessageType()){
            case NodeMessage.CONNECTION:
                break;
            case NodeMessage.TEXT:
                break;
            case NodeMessage.ACK:
                break;
            case NodeMessage.ALTERNATIVE:
                break;
            case NodeMessage.PERIOD_CHECK:
                break;
        }
    }
}
