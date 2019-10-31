package node;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public interface NodeInfo{
    InetAddress getInetAddress();
    int getPort();
    void refreshActivity();
    void setParent(String parentIP, int parentPort)  throws UnknownHostException;
    int getParentPort();
    InetAddress getParentAddress();
    Date getLastActivity();
    boolean replaceNode();
}
