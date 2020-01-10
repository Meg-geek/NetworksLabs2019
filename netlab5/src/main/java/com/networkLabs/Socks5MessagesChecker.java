package com.networkLabs;

import java.nio.ByteBuffer;

public class Socks5MessagesChecker implements Socks5Constants{
    private static final int CLIENT_GREETING_MIN_BYTES_AMOUNT = 3;
    /*version 1 byte + command 1 byte + reserved 1 byte + address type 1 byte = 4 bytes +
    destination address min 2 bytes + port number 2 bytes                    = 4 bytes
    = 8 bytes
     */
    private static final int CLIENT_CONNECTION_REQ_MIN_BYTES_LENGTH = 8;

    boolean isClientGreetingCorrect(ByteBuffer greetingBuffer){
        greetingBuffer.flip();
        if(greetingBuffer.limit() < CLIENT_GREETING_MIN_BYTES_AMOUNT){
            return false;
        }
        byte version = greetingBuffer.get();
        if(version != SOCKS_PROTOCOL_VERSION){
            return false;
        }
        int authNumb = greetingBuffer.get();
        while(authNumb > 0){
            int authMethod = greetingBuffer.get();
            if(authMethod == NO_AUTH){
                return true;
            }
            authNumb--;
        }
        return false;
    }

    boolean isClientCommandCorrect(ByteBuffer commandBuffer){
        commandBuffer.flip();
        if(commandBuffer.limit() < CLIENT_CONNECTION_REQ_MIN_BYTES_LENGTH){
            return false;
        }
        int version = commandBuffer.get();
        int commandCode = commandBuffer.get();
        int reserved = commandBuffer.get();
        int addressType = commandBuffer.get();
        return version == SOCKS_PROTOCOL_VERSION &&
                commandCode == COMMAND_ESTABLISH_TCP_IP_CONNECTION &&
                reserved == RESERVED_FIELD &&
                (addressType == IPV4_ADDRESS || addressType == DOMAIN_NAME_ADDRESS);
    }
}
