package model.networkUtils;

import model.utils.SettingConstants;

public interface GameNetworkSettings {
    //int DEFAULT_PING_DELAY_MS = 100;
    //int DEFAULT_NODE_TIMEOUT_MS = 800;
    SettingConstants<Integer> pingDelayMSConst = new SettingConstants<>(1, 100, 10000);
    SettingConstants<Integer> nodeTimeoutMSConst = new SettingConstants<>(1, 800, 10000);


    int getPingDelayMs();
    int getNodeTimeoutMs();

    void setPingDelayMs(int pingDelayMs);
    void setNodeTimeoutMs(int nodeTimeoutMs);

    boolean equals(GameNetworkSettings networkSettings);
    }
