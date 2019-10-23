package chatNode;

import node.NodeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class ChatNodeInfo implements NodeInfo {
    private int port, parentPort;
    private InetAddress address, parentAddress;
    private Date lastActivity;

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

    public Date getLastActivity() {
        return lastActivity;
    }

    public void refreshActivity(){
        lastActivity = new Date();
    }

    public void setParentPort(int parentPort){
        this.parentPort = parentPort;
    }

    public int getParentPort(){
        return parentPort;
    }

    public void setParentAddress(String ip) throws UnknownHostException{
        parentAddress = InetAddress.getByName(ip);
    }

    public InetAddress getParentAddress(){
        return parentAddress;
    }
}
