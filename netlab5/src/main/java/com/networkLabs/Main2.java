package com.networkLabs;

public class Main2 {
    private static final int MAX_PORT = 65535;
    private static final int MIN_PORT = 0;

    public static void main(String[] args){
        Socks5ProxyServer socks5ProxyServer;
        int port = MIN_PORT;
        if(args.length > 0){
            try {
                port = Integer.parseInt(args[0]);
                if(port < MIN_PORT || port > MAX_PORT){
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex){
                System.out.println("Wrong port format");
            }
        }
        if(port != MIN_PORT){
            socks5ProxyServer = new Socks5ProxyServer(port);
        } else {
            socks5ProxyServer = new Socks5ProxyServer();
        }
        try{
            socks5ProxyServer.run();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
