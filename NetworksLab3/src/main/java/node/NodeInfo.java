package node;

import java.net.InetAddress;

public interface NodeInfo {
    InetAddress getInetAddress();
    int getPort();
}
