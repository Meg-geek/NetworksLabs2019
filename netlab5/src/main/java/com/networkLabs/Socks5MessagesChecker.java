package com.networkLabs;

import java.nio.ByteBuffer;

public class Socks5MessagesChecker implements Socks5Constants{
    private static final int CLIENT_GREETING_MIN_BYTES_AMOUNT = 3;
    /*version 1 byte + command 1 byte + reserved 1 byte + addres type 1 byte = 4 bytes +
    destination address min 2 bytes + port number 2 bytes                    = 4 bytes
    = 8 bytes
     */
    private static final int CLIENT_CONNECTION_REQ_MIN_BYTES_LENGTH = 8;

    boolean isClientGreetingCorrect(ByteBuffer greetingBuffer){
        greetingBuffer.flip();
        if(greetingBuffer.limit() < CLIENT_GREETING_MIN_BYTES_AMOUNT){
            return false;
        }
        int version = greetingBuffer.get();
        if(version != SOCKS_PROTOCOL_VERSION){
            return false;
        }
        int authNumb = greetingBuffer.get();
        int authMethod = greetingBuffer.get();
        return (authNumb == AUTH_NUMB && authMethod == NO_AUTH);
    }

    boolean isClientConnectionReqCorrect(ByteBuffer connectionReqBuf){
        connectionReqBuf.flip();
        if(connectionReqBuf.limit() < CLIENT_GREETING_MIN_BYTES_AMOUNT){
            return false;
        }
        int version = connectionReqBuf.get();
        int commandCode = connectionReqBuf.get();
        int reserved = connectionReqBuf.get();
        return version == SOCKS_PROTOCOL_VERSION &&
                commandCode == COMMAND_ESTABLISH_TCP_IP_CONNECTION &&
                reserved == RESERVED_FIELD;
    }
}
