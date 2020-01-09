package com.networkLabs;

import com.networkLabs.api.ISocks5ConnectionHandler;
import com.networkLabs.api.ISocks5ProxyServer;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Socks5ProxyServer implements Runnable, ISocks5ProxyServer, Socks5Constants {
    private final static int STANDART_SOCKS5_PORT = 1080;
    private int port;
    private final String host = "127.0.0.1";
    private final static boolean NON_BLOCK_VALUE = false;

    private Selector selector;
    private ServerSocketChannel socketChannel;
    private DatagramChannel dnsDatagramChannel;

    private static final int DNS_PORT = 53;

    private Map<SocketChannel, ISocks5ConnectionHandler> channelConnectionHandlerMap = new HashMap<>();
    private Map<SocketChannel, ISocks5ConnectionHandler> channelConnectionWithDestMap = new HashMap<>();

    private Map<Integer, ISocks5ConnectionHandler> dnsMessageIdHandlerMap = new HashMap<>();

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
        } catch(IOException | SocksException ex){
            ex.printStackTrace();
        } finally {
            try {
                socketChannel.close();
                dnsDatagramChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSelectionKey(SelectionKey selectionKey) throws IOException, SocksException{
        if(selectionKey.isValid()){
            if(selectionKey.isAcceptable() && selectionKey.channel().equals(socketChannel)){
                acceptAndRegister();
            } else if (selectionKey.isConnectable()){
                ((SocketChannel)selectionKey.channel()).finishConnect();
            } else if (selectionKey.isReadable()){
                if(selectionKey.channel() instanceof SocketChannel){
                    readFromSocketChannel((SocketChannel) selectionKey.channel());
                }
                if(selectionKey.channel().equals(dnsDatagramChannel)){
                    readFromDnsChannel();
                }
            }
        }
    }

    private void acceptAndRegister() throws IOException{
        SocketChannel newChannel = socketChannel.accept();
        if(newChannel != null){
            newChannel.configureBlocking(NON_BLOCK_VALUE);
            newChannel.register(selector, SelectionKey.OP_READ);
            channelConnectionHandlerMap.put(newChannel, new Socks5ProxyConnectionHandler(newChannel, this));
        }
    }

    private void readFromSocketChannel(SocketChannel socketChannel) throws IOException, SocksException{
        ISocks5ConnectionHandler connectionHandler = channelConnectionHandlerMap.get(socketChannel);
        if(connectionHandler != null){
            connectionHandler.readClientData();
        } else {
            connectionHandler = channelConnectionWithDestMap.get(socketChannel);
            if(connectionHandler != null){
                connectionHandler.readDestData();
            }
        }
    }

    private void readFromDnsChannel() throws IOException, SocksException{
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesAmount = dnsDatagramChannel.read(byteBuffer);
        if(bytesAmount > 0){
            byteBuffer.flip();
            Message dnsMessage = new Message(byteBuffer.array());
            Record[] records = dnsMessage.getSectionArray(Section.ANSWER);
            for(Record record : records){
                if(record instanceof ARecord){
                    ARecord aRecord = (ARecord)record;
                    int id = dnsMessage.getHeader().getID();
                    ISocks5ConnectionHandler connectionHandler = dnsMessageIdHandlerMap.get(id);
                    if(connectionHandler != null && aRecord.getAddress() != null){
                        connectionHandler.connectToAddres(aRecord.getAddress());
                    }
                    dnsMessageIdHandlerMap.remove(id);
                }
            }
        }
    }

    @Override
    public void makeDnsRequest(String domainName, ISocks5ConnectionHandler connectionHandler)
            throws IOException {
        Name name = Name.fromString(domainName, Name.root);
        Record record = Record.newRecord(name, Type.A, DClass.IN);
        Message message = Message.newQuery(record);
        dnsDatagramChannel.write(ByteBuffer.wrap(message.toWire()));
        dnsMessageIdHandlerMap.put(message.getHeader().getID(), connectionHandler);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getAddress() {
        return host;
    }

    @Override
    public void addDestinationSocketChannel(SocketChannel destSocketChannel, ISocks5ConnectionHandler connectionHandler)
            throws IOException{
        if(destSocketChannel != null){
            destSocketChannel.configureBlocking(NON_BLOCK_VALUE);
            destSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
            channelConnectionWithDestMap.put(destSocketChannel, connectionHandler);
        }
    }
}
