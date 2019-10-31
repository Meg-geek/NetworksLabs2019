package node;

import node.nodeMessages.NodeMessage;

import java.util.List;

public interface Node {
    int TIMEOUT_MILSEC = 3000;
    int MESSAGE_CAPACITY = 100;

    void start();
    int getLossPerc();
}
