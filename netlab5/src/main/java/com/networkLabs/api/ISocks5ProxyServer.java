package com.networkLabs.api;

import com.networkLabs.Socks5ProxyConnectionHandler;
import org.xbill.DNS.TextParseException;

import java.io.IOException;

public interface ISocks5ProxyServer {
    void makeDnsRequest(String domainName, ISocks5ConnectionHandler connectionHandler) throws IOException;
    int getPort();
    String getAddress();
}
