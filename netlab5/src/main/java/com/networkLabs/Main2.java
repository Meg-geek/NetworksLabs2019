package com.networkLabs;

public class Main2 {
    public static void main(String[] args){
        try{
            Socks5ProxyServer socks5ProxyServer = new Socks5ProxyServer();
            socks5ProxyServer.run();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
