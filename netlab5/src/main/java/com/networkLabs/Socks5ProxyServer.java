package com.networkLabs;

import org.xbill.DNS.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Socks5ProxyServer implements Runnable, Socks5Constants {
    private final static int STANDART_SOCKS5_PORT = 1080;
    private int port;
    private final String host = "127.0.0.1";
    private final static boolean NON_BLOCK_VALUE = false;

    private Selector selector;
    private ServerSocketChannel socketChannel;
    private DatagramChannel dnsDatagramChannel;

    private static final int DNS_PORT = 53;

    private Map<SocketChannel, SocketChannel> connectionsMap = new HashMap<>();
    private Map<SocketChannel, ConnectionState> socketChannelStateMap = new HashMap<>();

    private Map<Integer, ConnectionReqInfo> dnsMessageIdConnectionReqMap = new HashMap<>();

    private Socks5MessagesChecker messagesChecker = new Socks5MessagesChecker();
    private Socks5MessageCreator messageCreator = new Socks5MessageCreator();
    private Socks5MessageParser messageParser = new Socks5MessageParser();

    Socks5ProxyServer(){
        this(STANDART_SOCKS5_PORT);
    }

    Socks5ProxyServer(int port){
        this.port = port;
    }

    @Override
    public void run() {
        try (Selector selector = Selector.open();
             ServerSocketChannel socketChannel = ServerSocketChannel.open();
             DatagramChannel dnsDatagramChannel = DatagramChannel.open()) {
            socketChannel.configureBlocking(NON_BLOCK_VALUE);
            socketChannel.socket().bind(new InetSocketAddress(host, this.port));
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);

            String[] dnsServers = ResolverConfig.getCurrentConfig().servers();
            dnsDatagramChannel.configureBlocking(NON_BLOCK_VALUE);
            dnsDatagramChannel.connect(new InetSocketAddress(dnsServers[0], DNS_PORT));
            dnsDatagramChannel.register(selector, SelectionKey.OP_READ);

            this.selector = selector;
            this.socketChannel = socketChannel;
            this.dnsDatagramChannel = dnsDatagramChannel;

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
            if(selectionKey.isAcceptable()){
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
            newChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
            socketChannelStateMap.put(newChannel, ConnectionState.ACCEPTED);
        }
    }

    private void readFromSocketChannel(SocketChannel socketChannel) throws IOException{
        ConnectionState connectionState = socketChannelStateMap.computeIfAbsent(socketChannel, k -> ConnectionState.ACCEPTED);
        if(connectionState == ConnectionState.ACCEPTED || connectionState == ConnectionState.NEED_CONNECTION){
            readReqFromClientSocketChannel(socketChannel, connectionState);
        }
        if(connectionState == ConnectionState.CONNECTED){
            readDataFromSocketChannel(socketChannel);
        }
    }

    private void readDataFromSocketChannel(SocketChannel socketChannel) throws IOException{
        SocketChannel connectedChannel = connectionsMap.get(socketChannel);
        if(connectedChannel.isConnected()){
            ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            try {
                int bytesRead = socketChannel.read(messageBuffer);
                if(bytesRead > 0){
                    connectedChannel.write(ByteBuffer.wrap(messageBuffer.array(), 0, bytesRead));
                }
                if(bytesRead == -1){
                    closeSocketChannel(socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeSocketChannel(socketChannel);
            }
        }
    }


    private void readReqFromClientSocketChannel(SocketChannel socketChannel, ConnectionState connectionState)
            throws IOException{
        ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = 0;
        try {
            bytesRead = socketChannel.read(messageBuffer);
        } catch (IOException ex) {
            ex.printStackTrace();
            closeSocketChannel(socketChannel);
        }
        if(bytesRead > 0){
            if(connectionState == ConnectionState.ACCEPTED
                    && messagesChecker.isClientGreetingCorrect(messageBuffer)){
                ByteBuffer responseMessage = messageCreator.createServerGreetingMessage();
                socketChannel.write(ByteBuffer.wrap(responseMessage.array(), 0,
                        Socks5MessageCreator.SERVER_GREETINGS_SIZE));
                socketChannelStateMap.put(socketChannel, ConnectionState.NEED_CONNECTION);
            } else if(connectionState == ConnectionState.NEED_CONNECTION
                    && messagesChecker.isClientCommandCorrect(messageBuffer)){
                connectSocketChannel(socketChannel, messageBuffer);
            }
        } else {
            socketChannelStateMap.remove(socketChannel);
            closeSocketChannel(socketChannel);
        }
    }

    private void connectSocketChannel(SocketChannel socketChannel, ByteBuffer messageBuffer) throws IOException{
        InetAddress address = messageParser.getAddress(messageBuffer);
        int port = messageParser.getPort(messageBuffer);
        if(address != null){
            connect(address, port, socketChannel);
        } else {
            //if DNS
            Name name = org.xbill.DNS.Name.fromString(messageParser.getDomainName(messageBuffer), Name.root);
            Record record = Record.newRecord(name, Type.A, DClass.IN);
            Message dnsMessage = Message.newQuery(record);
            dnsDatagramChannel.write(ByteBuffer.wrap(dnsMessage.toWire()));
            int destPort = messageParser.getPort(messageBuffer);
            dnsMessageIdConnectionReqMap.put(dnsMessage.getHeader().getID(),
                    new ConnectionReqInfo(socketChannel, destPort));
        }
    }

    private void connect(InetAddress address, int port, SocketChannel socketChannel) throws IOException{
        SocketChannel destinationSocketChannel = SocketChannel.open(new InetSocketAddress(address, port));
        ByteBuffer connectionAnswer = messageCreator
                .createServerConnectionResponse(destinationSocketChannel.isConnected(), port);
        if(!destinationSocketChannel.isConnected()){
            closeSocketChannel(socketChannel);
            return;
        }
        socketChannel.write(ByteBuffer.wrap(connectionAnswer.array(), 0,
                Socks5MessageCreator.SERVER_CONNECTION_RESPONSE_SIZE));
        destinationSocketChannel.configureBlocking(NON_BLOCK_VALUE);
        destinationSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
        connectionsMap.put(destinationSocketChannel, socketChannel);
        connectionsMap.put(socketChannel, destinationSocketChannel);
        socketChannelStateMap.put(destinationSocketChannel, ConnectionState.CONNECTED);
        socketChannelStateMap.put(socketChannel, ConnectionState.CONNECTED);
    }

    private void closeSocketChannel(SocketChannel socketChannel) throws IOException{
        SocketChannel relatedChannel = connectionsMap.get(socketChannel);
        if(relatedChannel != null){
            relatedChannel.close();
            connectionsMap.remove(relatedChannel);
            connectionsMap.remove(socketChannel);
        }
        socketChannel.close();
    }

    private void readFromDnsChannel() throws IOException{
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
                    InetAddress inetAddress = aRecord.getAddress();
                    ConnectionReqInfo connectionReqInfo = dnsMessageIdConnectionReqMap.get(id);
                    if(connectionReqInfo != null && aRecord.getAddress() != null){
                        connect(inetAddress,
                                connectionReqInfo.getDestinationPort(),
                                connectionReqInfo.getChannelToConnect());
                    }
                    dnsMessageIdConnectionReqMap.remove(id);
                }
            }
        }
    }
}
