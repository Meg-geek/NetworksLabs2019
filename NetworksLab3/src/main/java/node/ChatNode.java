package node;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class ChatNode implements Node {
    private String nodeName;
    private int lossPerc;
    private DatagramSocket socket;
    private List<NodeInfo> nearNodesInfoList;

    public ChatNode(String nodeName, int port, int lossPerc) throws SocketException {
        this.nodeName = nodeName;
        this.lossPerc = lossPerc;
        socket = new DatagramSocket(port);
        nearNodesInfoList = new LinkedList<>();
    }

    public ChatNode(String nodeName, int port, int lossPerc, String parentIP, int parentPort) throws SocketException,
            UnknownHostException {
        this(nodeName, port, lossPerc);
        nearNodesInfoList.add(new NodeInfo(parentIP, parentPort));
    }

    public void start() {

    }
}
