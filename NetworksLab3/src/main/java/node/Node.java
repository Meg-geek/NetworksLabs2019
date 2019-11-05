package node;

public interface Node {
    int TIMEOUT_MILSEC = 4000;
    int MESSAGE_CAPACITY = 100;

    void start();
    int getLossPerc();
}
