package node;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface NodeInfo {
    InetAddress getInetAddress();
    int getPort();
    void refreshActivity();
    void setParent(String parentIP, int parentPort)  throws UnknownHostException;
    int getParentPort();
    InetAddress getParentAddress();
}
