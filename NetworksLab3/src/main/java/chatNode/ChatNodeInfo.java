package chatNode;

import node.NodeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class ChatNodeInfo implements NodeInfo {
    private int port, parentPort;
    private InetAddress address, parentAddress = null;
    private Date lastActivity = new Date();

    ChatNodeInfo(String ip, int port) throws UnknownHostException {
        address = InetAddress.getByName(ip);
        this.port = port;
    }

    @Override
    public InetAddress getInetAddress(){
        return address;
    }

    @Override
    public int getPort(){
        return port;
    }

    @Override
    public Date getLastActivity() {
        return lastActivity;
    }

    @Override
    public boolean replaceNode() {
        if(parentAddress == null ||
            address.equals(parentAddress)){
            return false;
        }
        address = parentAddress;
        port = parentPort;
        return true;
    }

    @Override
    public void refreshActivity(){
        lastActivity = new Date();
    }

    @Override
    public void setParent(String parentIP, int parentPort) throws UnknownHostException{
        parentAddress = InetAddress.getByName(parentIP);
        this.parentPort = parentPort;
    }

    @Override
    public int getParentPort(){
        return parentPort;
    }

    @Override
    public InetAddress getParentAddress(){
        return parentAddress;
    }
}
