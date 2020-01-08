package com.networkLabs;

public interface Socks5Constants {
    int BUFFER_SIZE = 8192;
    int SOCKS_PROTOCOL_VERSION = 0x05;

    //greeting
    int AUTH_NUMB = 0x01;
    int NO_AUTH = 0x00;

    //field 4, address
    int IPV4_ADDRESS = 0x01;
    int DOMAIN_NAME_ADDRESS = 0x03;
    int IPV6_ADDRESS = 0x04;


}
