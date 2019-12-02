package model.utils;

public class ConvertionExeption extends Exception {
    public ConvertionExeption(){
        super();
    }

    public ConvertionExeption(String message){
        super("ConvertionException " + message);
    }

    public ConvertionExeption(Throwable cause){
        super(cause);
    }

    public ConvertionExeption(String message, Throwable cause){
        super(message, cause);
    }
}
