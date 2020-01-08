package com.networkLabs;

import java.nio.ByteBuffer;

public class Socks5MessagesChecker implements Socks5Constants{
    private static final int CLIENT_GREETING_MIN_BYTES_AMOUNT = 3;

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
}
