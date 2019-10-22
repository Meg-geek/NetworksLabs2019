package node;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeInfo {
    private int port;
    private InetAddress address;

    NodeInfo(String ip, int port) throws UnknownHostException {
        address = InetAddress.getByName(ip);
        this.port = port;
    }

    public InetAddress getInetAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }
}
