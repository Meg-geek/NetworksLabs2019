package model.networkUtils;

public interface GameNetworkSettings {
    int DEFAULT_PING_DELAY_MS = 100;
    int DEFAULT_NODE_TIMEOUT_MS = 800;

    int getPingDelayMs();
    int getNodeTimeoutMs();

    void setPingDelayMs(int pingDelayMs);
    void setNodeTimeoutMs(int nodeTimeoutMs);
    }
