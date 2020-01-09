package com.networkLabs;

public class SocksException extends Exception {
    SocksException(){
        super();
    }

    SocksException(String message){
        super(message);
    }

    SocksException(Throwable cause){
        super(cause);
    }

    SocksException(String message, Throwable cause){
        super(message, cause);
    }
}
