package com.networkLabs.api;

import com.networkLabs.Socks5ProxyConnectionHandler;
import org.xbill.DNS.TextParseException;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public interface ISocks5ProxyServer {
    void makeDnsRequest(String domainName, ISocks5ConnectionHandler connectionHandler) throws IOException;
    int getPort();
    String getAddress();
    void addDestinationSocketChannel(SocketChannel destSocketChannel, ISocks5ConnectionHandler connectionHandler)
            throws IOException;
}
