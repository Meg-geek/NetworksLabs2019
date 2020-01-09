package com.networkLabs.api;

import com.networkLabs.SocksException;

import java.io.IOException;
import java.net.InetAddress;

public interface ISocks5ConnectionHandler {
    void readClientData() throws SocksException, IOException;
    void readDestData() throws IOException;
    void connectToAddres(InetAddress address) throws IOException, SocksException;
}
