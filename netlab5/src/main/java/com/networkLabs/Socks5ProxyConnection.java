package com.networkLabs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socks5ProxyConnection implements Socks5Constants{
    private SocketChannel clientSocketChannel, destinationSocketChanel;
    private Socks5MessagesChecker messagesChecker;

    private static final int STATE_ACCEPTED = 0;
    private int clientState = STATE_ACCEPTED;

    Socks5ProxyConnection(SocketChannel socketChannel){
        this.clientSocketChannel = socketChannel;
        messagesChecker = new Socks5MessagesChecker();
    }

    void readData() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = clientSocketChannel.read(byteBuffer);
        byteBuffer.flip();
        if(clientState == STATE_ACCEPTED && bytesRead > 0){
            if(!messagesChecker.isClientGreetingCorrect(byteBuffer)){
                throw new IOException("Incorrect client greeting message");
                //throw new SocksException();
            }
        }
    }
}
