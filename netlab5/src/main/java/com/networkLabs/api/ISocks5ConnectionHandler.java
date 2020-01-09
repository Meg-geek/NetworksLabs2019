package com.networkLabs.api;

import com.networkLabs.SocksException;

import java.io.IOException;

public interface ISocks5ConnectionHandler {
    void readClientData() throws SocksException, IOException;
}
