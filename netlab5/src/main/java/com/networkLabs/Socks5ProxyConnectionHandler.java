package com.networkLabs;

import com.networkLabs.api.ISocks5ConnectionHandler;
import com.networkLabs.api.ISocks5ProxyServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socks5ProxyConnectionHandler implements Socks5Constants, ISocks5ConnectionHandler {
    private SocketChannel clientSocketChannel, destinationSocketChanel;
    private Socks5MessagesChecker messagesChecker;
    private ISocks5ProxyServer proxyServer;

    private InetAddress destinationInetAddress;
    private int destinationPort;

    private ClientState clientState = ClientState.ACCEPTED;

    private static final int GREETINGS_SIZE = 2;
    /*
        Socks version 1 byte + status 1 byte + reserved 1 byte + address type 1 byte +
        server bound address ip 4 bytes + port 2 bytes
     */
    private static final int SERVER_CONNECTION_RESPONSE_SIZE = 10;

    Socks5ProxyConnectionHandler(SocketChannel socketChannel, ISocks5ProxyServer proxyServer){
        this.clientSocketChannel = socketChannel;
        this.proxyServer = proxyServer;
        messagesChecker = new Socks5MessagesChecker();
    }

    @Override
                public void readClientData() throws SocksException, IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = clientSocketChannel.read(byteBuffer);
        byteBuffer.flip();
        if(bytesRead <= 0){
            throw new SocksException("Reached end of the clientSocketChannel");
        }
        switch (clientState){
            case ACCEPTED:
                handleClientGreeting(byteBuffer);
                break;
            case REGISTERED:
                handleClientConnectionReq(byteBuffer);
                break;
            case CONNECTED:
                if(clientSocketChannel.isConnected()){
                    destinationSocketChanel.write(byteBuffer);
                }
                break;
        }
    }

    @Override
    public void readDestData() throws IOException{
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        if(destinationSocketChanel.isConnected()){
            int bytesRead = destinationSocketChanel.read(byteBuffer);
            byteBuffer.flip();
            if(bytesRead > 0){
                clientSocketChannel.write(ByteBuffer.wrap(byteBuffer.array(), 0, bytesRead));
            }
        }
    }

    @Override
    public void connectToAddres(InetAddress address) throws IOException, SocksException{
        this.destinationInetAddress = address;
        this.connectToDestination();
    }

    private void handleClientConnectionReq(ByteBuffer clientConnectionReqBuf)
            throws SocksException, IOException {
        if(!messagesChecker.isClientGreetingCorrect(clientConnectionReqBuf)){
            throw new SocksException("Incorrect client connection request");
        }
        int addressType = clientConnectionReqBuf.get();
        if(addressType != IPV4_ADDRESS && addressType != DOMAIN_NAME_ADDRESS){
            throw new SocksException("Unsupported address type");
        }
        if(addressType == DOMAIN_NAME_ADDRESS){
            int domainNameLength = clientConnectionReqBuf.get();
            byte[] domainNameBytesArray = new byte[domainNameLength];
            clientConnectionReqBuf.get(domainNameBytesArray);
            proxyServer.makeDnsRequest(new String(domainNameBytesArray), this);
            this.destinationPort = clientConnectionReqBuf.getShort();
        }
        if(addressType == IPV4_ADDRESS){
            byte[] ipBytesArray = new byte[IPV4_ADDRESS_BYTES_AMOUNT];
            clientConnectionReqBuf.get(ipBytesArray);
            destinationInetAddress = InetAddress.getByAddress(ipBytesArray);
            this.destinationPort = clientConnectionReqBuf.getShort();
            connectToDestination();
        }

    }

    private void connectToDestination() throws IOException, SocksException{
        if(clientSocketChannel.isConnected()){
            destinationSocketChanel = SocketChannel.open(new InetSocketAddress(destinationInetAddress, destinationPort));

            ByteBuffer serverConnectionResponse = ByteBuffer.allocate(SERVER_CONNECTION_RESPONSE_SIZE);
            serverConnectionResponse.put(SOCKS_PROTOCOL_VERSION);
            byte status = destinationSocketChanel.isConnected()? STATUS_REQ_GRANTED : STATUS_GENERAL_FAILURE;
            serverConnectionResponse.put(status);
            serverConnectionResponse.put(RESERVED_FIELD);
            serverConnectionResponse.put(IPV4_ADDRESS);
            serverConnectionResponse.put(proxyServer.getAddress().getBytes());
            serverConnectionResponse.putShort((short)proxyServer.getPort());
            serverConnectionResponse.flip();
            clientSocketChannel.write(serverConnectionResponse);

            if(!destinationSocketChanel.isConnected()){
                throw new SocksException("Can't connect to the given address");
            }
            proxyServer.addDestinationSocketChannel(destinationSocketChanel, this);
            clientState = ClientState.CONNECTED;
        }
    }

    private void handleClientGreeting(ByteBuffer clientGreetingBuffer)
            throws SocksException, IOException{
        if(!messagesChecker.isClientGreetingCorrect(clientGreetingBuffer)){
            throw new SocksException("Incorrect client greeting message");
        }
        ByteBuffer serverGreetingsResponseBuf = ByteBuffer.allocate(GREETINGS_SIZE);
        serverGreetingsResponseBuf.put(SOCKS_PROTOCOL_VERSION);
        serverGreetingsResponseBuf.put(NO_AUTH);
        serverGreetingsResponseBuf.flip();
        clientSocketChannel.write(serverGreetingsResponseBuf);
        clientState = ClientState.REGISTERED;
    }
}
