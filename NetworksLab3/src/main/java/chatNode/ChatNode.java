package chatNode;

import chatNode.helpers.*;
import node.MessagesNode;
import node.NodeInfo;
import node.NodeMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatNode implements MessagesNode {
    private String nodeName;
    private int lossPerc;
    private DatagramSocket socket;
    private List<NodeInfo> nearNodesInfoList = new CopyOnWriteArrayList<>();
    private NodeInfo alernativeNode;
   // private Queue<NodeMessage> sentMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private Queue<NodeMessage> recvMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private Queue<String> printMessagesUUIDQueue = new ArrayDeque<>(MESSAGE_CAPACITY);
    private final int SENDER_THREADS_AMOUNT = 3;
    //private ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_AMOUNT);
    private MessageSender messageSender;
    private boolean started = false;
    private final int THREADS_AMOUNT = 2;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private final int INIT_DELAY = 0;
    private final int CONSOLE_LISTENER_DELAY = 200;
    private Thread recieveThread;
    private final int CONNECTION_CONTROLLER_DELAY = TIMEOUT_MILSEC*2;
    private final int ACK_MANAGER_DELAY = 500;

    public ChatNode(String nodeName, int port, int lossPerc) throws SocketException {
        this.nodeName = nodeName;
        this.lossPerc = lossPerc;
        socket = new DatagramSocket(port);
        messageSender = new MessageSender(socket, SENDER_THREADS_AMOUNT);
    }

    public ChatNode(String nodeName, int port, int lossPerc, String parentIP, int parentPort) throws IOException {
        this(nodeName, port, lossPerc);
        alernativeNode = new ChatNodeInfo(parentIP, parentPort);
        nearNodesInfoList.add(alernativeNode);
        sendMessage(new ChatNodeMessage(NodeMessage.CONNECTION), alernativeNode);
    }

    //private void recieveMessages(){ }

    public void start() {
        if(started){
            return;
        }
        started = true;
        recieveThread = new Thread(new MessageReciever(socket, this));
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new ConsoleListener(this),
                INIT_DELAY, CONSOLE_LISTENER_DELAY, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new ConnectionController(nearNodesInfoList),
                INIT_DELAY, CONNECTION_CONTROLLER_DELAY, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new ACKManager(this),
                INIT_DELAY, ACK_MANAGER_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendMessage(NodeMessage message, NodeInfo nodeInfo) {
        if(message != null && nodeInfo != null){
            messageSender.sendMessage(message, nodeInfo);
        }
    }

    @Override
    public void sendMessage(NodeMessage message, List<NodeInfo> nodesInfoList) {
        if(message != null && nearNodesInfoList != null &&
            nearNodesInfoList.size() > 0){
            messageSender.sendMessage(message, nodesInfoList);
        }
    }

    @Override
    public void sendMessage(NodeMessage message) {
        if(message != null){
            messageSender.sendMessage(message, nearNodesInfoList);
        }
    }

    /*
        private void handleMessage(NodeMessage message){
            switch(message.getMessageType()){
                case NodeMessage.CONNECTION:
                    addNode(message.getIP(), message.getPort());
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
    */
    @Override
    public void addRecvMessage(NodeMessage message){
        if(recvMessageQueue.size() == MESSAGE_CAPACITY){
            recvMessageQueue.poll();
        }
        recvMessageQueue.add(message);
        //handleMessage(message);
        sendACK(message);
    }

    private void sendACK(NodeMessage message){
      //  NodeMessage ACKmessage = new ChatNodeMessage();
        //messageSender.sendMessage(ACKmessage, nearNodesInfoList);
    }

    private void printMessage(NodeMessage message){
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

    @Override
    public int getLossPerc(){
        return lossPerc;
    }

    public void sendText(String text) throws IOException{
        NodeMessage message = new ChatNodeMessage(NodeMessage.TEXT, text);
        addPrintedMessage(message.getUUID());
        sendMessage(message);
    }

    private boolean addPrintedMessage(String uuid){
        if(printMessagesUUIDQueue.contains(uuid)){
            return false;
        }
        if(printMessagesUUIDQueue.size() >= MESSAGE_CAPACITY){
            printMessagesUUIDQueue.poll();
        }
        printMessagesUUIDQueue.add(uuid);
        return true;
    }
}
