package com.networkLabs;

import org.xbill.DNS.ResolverConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Socks5ProxyServer implements Runnable{
    private final static int STANDART_SOCKS5_PORT = 1080;
    private int port;
    private final String host = "127.0.0.1";
    private final static boolean NON_BLOCK_VALUE = false;

    private Selector selector;
    private ServerSocketChannel socketChannel;
    private DatagramChannel dnsDatagramChannel;

    private static final int DNS_PORT = 53;

    private Map<SocketChannel, Socks5ProxyConnection> channelClientMap = new HashMap<>();

    Socks5ProxyServer() throws IOException{
        this(STANDART_SOCKS5_PORT);
    }

    Socks5ProxyServer(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
        socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(NON_BLOCK_VALUE);
        socketChannel.socket().bind(new InetSocketAddress(host, this.port));
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //dns
        String[] dnsServers = ResolverConfig.getCurrentConfig().servers();
        dnsDatagramChannel = DatagramChannel.open();
        dnsDatagramChannel.configureBlocking(NON_BLOCK_VALUE);
        //?
        dnsDatagramChannel.connect(new InetSocketAddress(dnsServers[0], DNS_PORT));
        dnsDatagramChannel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void run() {
        try {
            while(selector.select() >= 0){
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                for(SelectionKey selectionKey : selectionKeySet){
                    handleSelectionKey(selectionKey);
                }
            }
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void handleSelectionKey(SelectionKey selectionKey) throws IOException{
        if(selectionKey.isValid()){
            if(selectionKey.isAcceptable() && selectionKey.channel().equals(socketChannel)){
                acceptAndRegister();
            } else if (selectionKey.isConnectable()){
                ((SocketChannel)selectionKey.channel()).finishConnect();
            } else if (selectionKey.isReadable()){
                if(selectionKey.channel() instanceof SocketChannel){
                    readFromSocketChannel((SocketChannel) selectionKey.channel());
                }
            }
        }
    }

    private void acceptAndRegister() throws IOException{
        SocketChannel newChannel = socketChannel.accept();
        if(newChannel != null){
            newChannel.configureBlocking(NON_BLOCK_VALUE);
            newChannel.register(selector, SelectionKey.OP_READ);
            channelClientMap.put(newChannel, new Socks5ProxyConnection(newChannel));
        }
    }

    private void readFromSocketChannel(SocketChannel socketChannel) throws IOException{
        Socks5ProxyConnection proxyClient = channelClientMap.get(socketChannel);
        if(proxyClient != null){
            proxyClient.readData();
        }
    }

    private void readFromDnsChannel(SelectionKey selectionKey){}
}
