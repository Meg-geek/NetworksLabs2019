package com.networkLabs;

import java.nio.ByteBuffer;

public class Socks5MessageCreator implements Socks5Constants{
    private byte[] ipv4Address = "127.0.0.1".getBytes();
    private static final int SERVER_GREETINGS_SIZE = 2;
    /*
       Socks version 1 byte + status 1 byte + reserved 1 byte + address type 1 byte +
       server bound address ip 4 bytes + port 2 bytes
    */
    private static final int SERVER_CONNECTION_RESPONSE_SIZE = 10;

     ByteBuffer createServerGreetingMessage(){
        ByteBuffer serverGreetingsResponseBuf = ByteBuffer.allocate(SERVER_GREETINGS_SIZE);
        serverGreetingsResponseBuf.put(SOCKS_PROTOCOL_VERSION);
        serverGreetingsResponseBuf.put(NO_AUTH);
        serverGreetingsResponseBuf.flip();
        return serverGreetingsResponseBuf;
    }

    ByteBuffer createServerConnectionResponse(boolean connectedStatus, int port){
        ByteBuffer serverConnectionResponse = ByteBuffer.allocate(SERVER_CONNECTION_RESPONSE_SIZE);
        serverConnectionResponse.put(SOCKS_PROTOCOL_VERSION);
        byte status = connectedStatus ? STATUS_REQ_GRANTED : STATUS_GENERAL_FAILURE;
        serverConnectionResponse.put(status);
        serverConnectionResponse.put(RESERVED_FIELD);
        serverConnectionResponse.put(IPV4_ADDRESS);
        serverConnectionResponse.put(ipv4Address);
        serverConnectionResponse.putShort((short)port);
        serverConnectionResponse.flip();
        return serverConnectionResponse;
    }
}
