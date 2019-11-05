package chatNode;

import chatNode.helpers.*;
import node.MessagesNode;
import node.NodeInfo;
import node.NodeMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private CopyOnWriteArrayList<NodeInfo> nearNodesInfoList = new CopyOnWriteArrayList<>();
    private NodeInfo alernativeNode;
    private Queue<NodeMessage> recvMessageQueue = new ArrayBlockingQueue<>(MESSAGE_CAPACITY);
    private Queue<String> printMessagesUUIDQueue = new ArrayDeque<>(MESSAGE_CAPACITY);
    private final int SENDER_THREADS_AMOUNT = 3;
    private MessageSender messageSender;
    private boolean started = false;
    private final int THREADS_AMOUNT = 3;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(THREADS_AMOUNT);
    private final int INIT_DELAY = 0;
    private final int CONSOLE_LISTENER_DELAY = 100;
    private Thread recieveThread;
    private final int CONNECTION_CONTROLLER_DELAY = TIMEOUT_MILSEC/2;
    private final int ACK_MANAGER_DELAY = 500;
    private ACKManager ACKManager = new ACKManager(this);

    public ChatNode(String nodeName, int port, int lossPerc) throws SocketException, UnknownHostException{
        this.nodeName = nodeName;
        this.lossPerc = lossPerc;
        socket = new DatagramSocket(port, InetAddress.getLocalHost());
        messageSender = new MessageSender(socket, SENDER_THREADS_AMOUNT);
        System.out.println("IP" + socket.getLocalSocketAddress());
    }

    public ChatNode(String nodeName, int port, int lossPerc, String parentIP, int parentPort) throws IOException {
        this(nodeName, port, lossPerc);
        alernativeNode = new ChatNodeInfo(parentIP, parentPort);
        nearNodesInfoList.add(alernativeNode);
        sendMessage(new ChatNodeMessage(NodeMessage.CONNECTION), alernativeNode);
    }

    public void start() {
        if(started){
            return;
        }
        started = true;
        recieveThread = new Thread(new MessageReciever(socket, this));
        recieveThread.start();
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new ConsoleListener(this),
                INIT_DELAY, CONSOLE_LISTENER_DELAY, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new ConnectionController(nearNodesInfoList, this),
                INIT_DELAY, CONNECTION_CONTROLLER_DELAY, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(ACKManager,
                INIT_DELAY, ACK_MANAGER_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendMessage(NodeMessage message, NodeInfo nodeInfo) {
        if(message != null && nodeInfo != null){
            messageSender.sendMessage(message, nodeInfo);
            ACKManager.addMessage(message, nodeInfo);
        }
    }

    @Override
    public void sendMessage(NodeMessage message, List<NodeInfo> nodesInfoList) {
        if(message != null && nodesInfoList != null &&
            nodesInfoList.size() > 0){
            messageSender.sendMessage(message, nodesInfoList);
            ACKManager.addMessage(message, nodesInfoList);
        }
    }

    @Override
    public void sendMessage(NodeMessage message) {
        if(message != null && nearNodesInfoList.size() > 0){
            List<NodeInfo> copiedList = (CopyOnWriteArrayList<NodeInfo>)nearNodesInfoList.clone();
            messageSender.sendMessage(message, copiedList);
            ACKManager.addMessage(message, copiedList);
        }
    }

    private void handleMessage(NodeMessage message){
        refreshNodeActivity(message.getIP(), message.getPort());
        switch(message.getMessageType()){
            case NodeMessage.CONNECTION:
                addNode(message.getIP(), message.getPort());
                break;
            case NodeMessage.TEXT:
                printMessage(message);
                break;
            case NodeMessage.ACK:
                ackRecv(message.getACKUUID(), message.getIP(), message.getPort());
                break;
            case NodeMessage.ALTERNATIVE:
                setNodeParent(message.getIP(),
                        message.getPort(),
                        message.getParentIP(),
                        message.getParentPort());
                break;
            case NodeMessage.PERIOD_CHECK:
                break;
        }
    }

    private void ackRecv(String messageUUID, String ip, int port){
        NodeInfo nodeInfo = findNode(ip, port);
        if(nodeInfo != null){
            ACKManager.ackRecieved(messageUUID, nodeInfo);
        }
    }

    @Override
    public void addRecvMessage(NodeMessage message){
        if(recvMessageQueue.size() == MESSAGE_CAPACITY){
            recvMessageQueue.poll();
        }
        try {
            sendACK(message);
            recvMessageQueue.add(message);
            handleMessage(message);
        } catch(IOException ex){
            //ex.printStackTrace();
        }
    }

    private void sendACK(NodeMessage message) throws IOException{
        NodeMessage ACKmessage = new ChatNodeMessage(NodeMessage.ACK, message.getUUID());
        messageSender.sendMessage(ACKmessage, nearNodesInfoList);
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
            if(alernativeNode == null){
                alernativeNode = nodeInfo;
                sendMessage(new ChatNodeMessage(NodeMessage.ALTERNATIVE,
                        nodeInfo.getInetAddress().getHostAddress(),
                        nodeInfo.getPort()));
            }
            nearNodesInfoList.add(nodeInfo);
        } catch(IOException ex){
            //log exception?
        }
    }

    private NodeInfo findNode(String ip, int port){
        NodeInfo nodeInfo = null;
        Iterator<NodeInfo> iterator = nearNodesInfoList.iterator();
        while(iterator.hasNext() && nodeInfo == null){
            NodeInfo nodeIt = iterator.next();
            if (nodeIt.getInetAddress().getHostAddress().equals(ip)
                && nodeIt.getPort() == port){
                nodeInfo = nodeIt;
            }
        }
        return nodeInfo;
    }

    private void setNodeParent(String ip, int port, String parentIP, int parentPort){
        NodeInfo nodeInfo = findNode(ip, port);
        try{
            if(nodeInfo != null){
                nodeInfo.setParent(parentIP, parentPort);
            }
        } catch(UnknownHostException ex) {
            //log?
        }
    }

    private void refreshNodeActivity(String ip, int port){
        NodeInfo nodeInfo = findNode(ip, port);
        if(nodeInfo != null){
            nodeInfo.refreshActivity();
        }
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

    public NodeInfo getAlernativeNode(){
        return alernativeNode;
    }

    public void setAlernativeNode(NodeInfo newAlternativeNode){
        if(nearNodesInfoList.contains(newAlternativeNode)){
            alernativeNode = newAlternativeNode;
            try{
                sendMessage(new ChatNodeMessage(NodeMessage.ALTERNATIVE,
                        alernativeNode.getInetAddress().getHostAddress(),
                        alernativeNode.getPort()));
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        if(nearNodesInfoList.size() == 0){
            alernativeNode = null;
        }
    }
}
