package com.networkLabs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Socks5ProxyServer implements Runnable{
    private final int BUFFER_SIZE = 8192;

    private final static int STANDART_SOCKS5_PORT = 1080;
    private int port;
    private final String host = "127.0.0.1";
    private final static boolean BLOCK_VALUE = false;

    private Selector selector;
    private ServerSocketChannel socketChannel;
    private DatagramChannel dnsDatagramChannel;

    Socks5ProxyServer() throws IOException{
        this(STANDART_SOCKS5_PORT);
    }

    Socks5ProxyServer(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
        socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(BLOCK_VALUE);
        socketChannel.socket().bind(new InetSocketAddress(host, this.port));
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //dns

    }

    @Override
    public void run() {

    }

}
