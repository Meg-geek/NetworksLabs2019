package com.networkLabs;

import java.nio.channels.SocketChannel;

public class ConnectionReqInfo {
    private SocketChannel channelToConnect;
    private int destinationPort;

    ConnectionReqInfo(SocketChannel channelToConnect, int port){
        this.channelToConnect = channelToConnect;
        destinationPort = port;
    }

    int getDestinationPort() {
        return destinationPort;
    }

    SocketChannel getChannelToConnect() {
        return channelToConnect;
    }
}
