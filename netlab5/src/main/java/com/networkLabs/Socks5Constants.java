package com.networkLabs;

public interface Socks5Constants {
    int BUFFER_SIZE = 8192;
    byte SOCKS_PROTOCOL_VERSION = 0x05;

    //greeting
    byte AUTH_NUMB = 0x01;
    byte NO_AUTH = 0x00;

    //command code
    byte COMMAND_ESTABLISH_TCP_IP_CONNECTION = 0x01;

    byte RESERVED_FIELD = 0x00;

    //field 4, address
    byte IPV4_ADDRESS = 0x01;
    byte DOMAIN_NAME_ADDRESS = 0x03;
    byte IPV6_ADDRESS = 0x04;

    int IPV4_ADDRESS_BYTES_AMOUNT = 4;

    //for server response
    byte STATUS_REQ_GRANTED = 0x00;
    byte STATUS_GENERAL_FAILURE = 0x01;

}
