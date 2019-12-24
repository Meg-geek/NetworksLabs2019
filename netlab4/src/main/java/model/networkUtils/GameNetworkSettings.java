package model.networkUtils;

import model.utils.SettingConstants;

public interface GameNetworkSettings {
    int MULTICAST_INTERVAL_S = 1;
    String MULTICAST_ADDRESS = "239.192.0.4";
    int MULTICAST_PORT = 9192;
    SettingConstants<Integer> pingDelayMSConst = new SettingConstants<>(1, 100, 10000);
    SettingConstants<Integer> nodeTimeoutMSConst = new SettingConstants<>(1, 800, 10000);


    int getPingDelayMs();
    int getNodeTimeoutMs();

    void setPingDelayMs(int pingDelayMs);
    void setNodeTimeoutMs(int nodeTimeoutMs);

    boolean equals(GameNetworkSettings networkSettings);
    }
