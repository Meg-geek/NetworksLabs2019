package com.networkLabs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Socks5MessageParser implements Socks5Constants{
    /*version 1 byte + command 1 byte + reserved 1 byte + address type 1 byte = 4 bytes +
    destination address min 2 bytes + port number 2 bytes                    = 4 bytes
    = 8 bytes
     */
    private static final int ADDRESS_TYPE_INDEX = 3;
    private static final int DESTINATION_IPV4_ADDRESS_LENGTH = 4;
    private static final int PORT_INDEX_IPV4 = 8;
    private static final int DOMAIN_NAME_LENGTH_INDEX = ADDRESS_TYPE_INDEX + 1;
    private static final int DOMAIN_NAME_START_INDEX = DOMAIN_NAME_LENGTH_INDEX + 1;

    //null if DNS
    InetAddress getAddress(ByteBuffer messageBuffer) throws UnknownHostException {
        int addresType = messageBuffer.get(ADDRESS_TYPE_INDEX);
        if(addresType == DOMAIN_NAME_ADDRESS){
            return null;
        }
        byte[] address = new byte[DESTINATION_IPV4_ADDRESS_LENGTH];
        for(int i = 1; i < DESTINATION_IPV4_ADDRESS_LENGTH + 1; i++){
            address[i] = messageBuffer.get(ADDRESS_TYPE_INDEX + i);
        }
        return InetAddress.getByName(new String(address));
    }

    int getPort(ByteBuffer messageBuffer){
        int addressType = messageBuffer.get(ADDRESS_TYPE_INDEX);
        if(addressType == IPV4_ADDRESS){
            return messageBuffer.getShort(PORT_INDEX_IPV4);
        }
        int domainNameLength = messageBuffer.get(DOMAIN_NAME_LENGTH_INDEX);
        return messageBuffer.getShort(DOMAIN_NAME_LENGTH_INDEX + domainNameLength);
    }

    String getDomainName(ByteBuffer messageBuffer){
        int addressType = messageBuffer.get(ADDRESS_TYPE_INDEX);
        if(addressType == IPV4_ADDRESS){
            return null;
        }
        int domainNameLength = messageBuffer.get(DOMAIN_NAME_LENGTH_INDEX);
        byte[] domainNameByteArray = new byte[domainNameLength];
        for(int i = 0; i < domainNameLength; i++){
            domainNameByteArray[i] = messageBuffer.get(DOMAIN_NAME_START_INDEX + i);
        }
        return new String(domainNameByteArray);
    }
}
